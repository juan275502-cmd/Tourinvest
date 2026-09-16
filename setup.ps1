<#
TourInvest - setup.ps1 (equivalente NATIVO de Windows a setup.sh)

Este equipo no tiene Git Bash ni WSL, por lo que setup.sh no puede ejecutarse;
usa este script. Detecta automaticamente las rutas reales de mysql.exe y
mvn.cmd (incluido el Maven incluido en la extension Oracle Java de VS Code).

USO (PowerShell):
  .\setup.ps1 check         # verifica JDK, Maven, MySQL, Node y Python
  .\setup.ps1 seed          # carga query.sql (raiz) en la BD 'tourinvest'
  .\setup.ps1 run-backend   # arranca Spring Boot (:8080)
  .\setup.ps1 run-frontend  # servidor estatico (:8081) y abre el navegador
  .\setup.ps1 help

Si PowerShell bloquea scripts de esta carpeta, ejecute:
  powershell -NoProfile -ExecutionPolicy Bypass -File .\setup.ps1 check
#>
param(
    [Parameter(Position = 0)][string]$Action = "help",
    [string]$MySqlExe = "",
    [string]$MvnCmd = ""
)

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$script:CheckOk = $true

function Resolve-Mysql([string]$Override) {
    if ($Override -and (Test-Path $Override)) { return $Override }
    foreach ($v in @("9.6", "9.4", "9.2", "8.4", "8.0")) {
        $p = "C:\Program Files\MySQL\MySQL Server $v\bin\mysql.exe"
        if (Test-Path $p) { return $p }
    }
    $maria = Get-Item "C:\Program Files\MariaDB *\bin\mysql.exe" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($maria) { return $maria.FullName }
    $cmd = Get-Command mysql -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    return $null
}

function Resolve-Mvn([string]$Override) {
    if ($Override -and (Test-Path $Override)) { return $Override }
    $cmd = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if (-not $cmd) { $cmd = Get-Command mvn -ErrorAction SilentlyContinue }
    if ($cmd) { return $cmd.Source }
    # Fallback: Maven incluido con la extension Oracle Java (VS Code)
    $extRoot = Join-Path $env:USERPROFILE ".vscode\extensions"
    if (Test-Path $extRoot) {
        foreach ($d in Get-ChildItem $extRoot -Directory -ErrorAction SilentlyContinue) {
            $p = Join-Path $d.FullName "nbcode\java\maven\bin\mvn.cmd"
            if (Test-Path $p) { return $p }
        }
    }
    return $null
}

function Resolve-Python {
    $venvPy = Join-Path $Root ".venv\Scripts\python.exe"
    if (Test-Path $venvPy) { return $venvPy }   # preferimos el .venv del repositorio
    $cmd = Get-Command python -ErrorAction SilentlyContinue
    if (-not $cmd) { $cmd = Get-Command py -ErrorAction SilentlyContinue }
    if ($cmd) { return $cmd.Source }
    return $null
}

# Resolucion real de binarios (una sola vez)
$script:Mvn    = Resolve-Mvn $MvnCmd
$script:MySql  = Resolve-Mysql $MySqlExe
$script:Python = Resolve-Python

function Test-RunningService {
    Get-Service -ErrorAction SilentlyContinue | Where-Object { $_.DisplayName -match 'mysql|maria' }
}

function Show-Check {
    Write-Host "== TourInvest - check de requisitos ==" -ForegroundColor Cyan
    $script:CheckOk = $true

    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if ($javaCmd) {
        $v = (& java -version 2>&1 | Select-Object -First 1)
        Write-Host "  [OK] java   -> $($javaCmd.Source)"
        Write-Host "         $v"
    } else { Write-Host "  [FALTA] java"; $script:CheckOk = $false }

    if ($script:Mvn) { Write-Host "  [OK] mvn    -> $($script:Mvn)" }
    else { Write-Host "  [FALTA] mvn"; $script:CheckOk = $false }

    if ($script:MySql) {
        Write-Host "  [OK] mysql  -> $($script:MySql)"
        Test-RunningService | ForEach-Object { Write-Host "         servicio '$($_.Name)': $($_.Status)" }
    } else { Write-Host "  [FALTA] mysql"; $script:CheckOk = $false }

    $nodeCmd = Get-Command node -ErrorAction SilentlyContinue
    if ($nodeCmd) { Write-Host "  [OK] node   -> $($nodeCmd.Source)" }
    else { Write-Host "  [--] node (opcional, solo validar sintaxis JS)" }

    if ($script:Python) {
        $pyv = (& $script:Python --version 2>&1)
        Write-Host "  [OK] python -> $($script:Python)"
        Write-Host "         $pyv"
    } else { Write-Host "  [--] python (opcional, solo la replica Flask :5000)" }

    if ($script:CheckOk) {
        Write-Host ""
        Write-Host "Todo listo. Pasos:" -ForegroundColor Green
        Write-Host "  .\setup.ps1 seed            # si aun no cargaste la BD"
        Write-Host "  .\setup.ps1 run-backend     # API     http://localhost:8080"
        Write-Host "  .\setup.ps1 run-frontend    # sitio   http://localhost:8081/login.html"
    } else {
        Write-Host ""
        Write-Host "Atiende las herramientas marcadas como [FALTA]." -ForegroundColor Yellow
    }
}
function Invoke-Seed {
    Write-Host "== Cargando esquema + datos de prueba (query.sql, raiz del proyecto) ==" -ForegroundColor Cyan
    if (-not $script:MySql) {
        Write-Host "mysql.exe no encontrado. Instala MySQL/MariaDB o pasa -MySqlExe <ruta>." -ForegroundColor Red
        exit 1
    }
    $sql = Join-Path $Root "query.sql"
    if (-not (Test-Path $sql)) {
        Write-Host "No se encontro $sql" -ForegroundColor Red
        exit 1
    }
    # Pide la contrasena de root de forma interactiva.
    Get-Content $sql | & $script:MySql --default-character-set=utf8mb4 -u root -p tourinvest
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "  OK. Usuarios semilla (clave 123456):" -ForegroundColor Green
        Write-Host "    nuevo@tourinvest.com   (Administrador; query.sql renombra el original juan@)"
        Write-Host "    laura@tourinvest.com   (Analista)"
        Write-Host "    carlos@tourinvest.com  (Inversionista)"
    }
}

function Invoke-RunBackend {
    if (-not $script:Mvn) {
        Write-Host "mvn.cmd no encontrado. Instala Maven o pasa -MvnCmd <ruta>." -ForegroundColor Red
        exit 1
    }
    $props = Join-Path $Root "backend-java\src\main\resources\application.properties"
    $content = Get-Content $props -Raw
    $changedPw = $false
    if ($content -match 'spring\.datasource\.password=TU_PASSWORD') {
        Write-Host ">> Contrasena de MySQL sin configurar (spring.datasource.password=TU_PASSWORD)." -ForegroundColor Yellow
        $sec = Read-Host "   Escribe la contrasena de root (ENTER si root NO tiene)" -AsSecureString
        $bss = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec)
        $plain = [Runtime.InteropServices.Marshal]::PtrToStringAuto($bss)
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bss)
        $content = $content -replace '(?m)^spring\.datasource\.password=.*$', ("spring.datasource.password=" + $plain)
        Set-Content -Path $props -Value $content
        Write-Host ">> Guardada en application.properties (archivo local del proyecto)." -ForegroundColor Green
        $changedPw = $true
        # Si la BD 'tourinvest' todavia no existe, se crea y se siembra AHORA.
        # Nunca ejecuta query.sql sobre una BD ya existente (evita borrar datos).
        $null = & $script:MySql -u root "--password=$plain" -e "USE tourinvest;" 2>$null
        if ($LASTEXITCODE -ne 0) {
            $null = & $script:MySql -u root "--password=$plain" -e "SHOW DATABASES;" 2>$null
            if ($LASTEXITCODE -eq 0) {
                Get-Content (Join-Path $Root "query.sql") | & $script:MySql --default-character-set=utf8mb4 -u root "--password=$plain" tourinvest 2>$null
                Write-Host ">> BD 'tourinvest' creada y sembrada con usuarios de prueba (clave 123456)." -ForegroundColor Green
            } else {
                Write-Host ">> La contrasena de root no fue aceptada por MySQL; revísala e inténtalo de nuevo." -ForegroundColor Red
            }
        }
    }
    # Push/Pop-Location: NO dejamos la terminal del usuario dentro de backend-java
    Push-Location (Join-Path $Root "backend-java")
    try {
        Write-Host "API escuchando en http://localhost:8080" -ForegroundColor Cyan
        & $script:Mvn spring-boot:run
    } finally {
        Pop-Location   # restaura la carpeta del usuario al salir
    }
}

function Invoke-RunFrontend {
    if (-not $script:Python) {
        Write-Host "python no disponible. Instala Python 3 para servir el frontend estatico." -ForegroundColor Red
        exit 1
    }
    $url = "http://localhost:8081/login.html"
    Write-Host "Frontend en $url  (abriendo navegador...)" -ForegroundColor Cyan
    Start-Process $url   # abre el navegador automaticamente
    Push-Location (Join-Path $Root "tourinvest")
    try {
        & $script:Python -m http.server 8081
    } finally {
        Pop-Location
    }
}

function Show-Help {
    Write-Host "Uso: .\setup.ps1 check | seed | run-backend | run-frontend | set-password | help" -ForegroundColor Cyan
    Write-Host "  check         Verifica java/mvn/mysql/node/python y muestra servicios MySQL."
    Write-Host "  seed          Carga query.sql (raiz) en la BD tourinvest. [pedira password]"
    Write-Host "  run-backend   Compila/ejecuta Spring Boot en :8080. Pide la contrasena MySQL"
    Write-Host "                si no esta configurada y siembra la BD solo si aun no existe."
    Write-Host "  run-frontend  Sirve ./frontend en :8081 y abre el navegador."
    Write-Host "  set-password  Reinicia spring.datasource.password para que run-backend la pida de nuevo."
    Write-Host "Extras: -MySqlExe <ruta> y -MvnCmd <ruta> para forzar binarios concretos."
}

switch ($Action.ToLowerInvariant()) {
    "check"        { Show-Check;        exit ([int](-not $script:CheckOk)) }
    "seed"         { Invoke-Seed }
    "run-backend"  { Invoke-RunBackend }
    "run-frontend" { Invoke-RunFrontend }
    "set-password" {
        $props = Join-Path $Root "backend-java\src\main\resources\application.properties"
        (Get-Content $props -Raw) -replace '(?m)^spring\.datasource\.password=.*$', 'spring.datasource.password=TU_PASSWORD' | Set-Content $props
        Write-Host "Reseteado. Ejecuta .\setup.ps1 run-backend y escribe la contrasena cuando la pida." -ForegroundColor Green
        exit 0
    }
    "help"         { Show-Help }
    default        { Write-Host "Accion desconocida: $Action" -ForegroundColor Yellow; Show-Help; exit 2 }
}