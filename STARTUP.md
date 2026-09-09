# STARTUP — Montar TourInvest paso a paso

**Arquitectura canónica:** `Java Spring Boot (:8080)` + `MySQL` + `frontend estático (HTML/CSS/JS)`.
`backend-python` es **réplica coherente** del modelo de datos (vocabulario alineado `Activo/Inactivo`, roles `Title-case`) y **no** es la API del frontend.

> Este documento está adaptado para **Windows (PowerShell)**. Las alternativas para Linux/macOS se indican con `🐧`.
> En **Windows NO uses `setup.sh`** (este equipo no tiene Git Bash ni WSL): usa el equivalente nativo **`setup.ps1`**.

---

## ⚡ Opción 0 — LA MÁS FÁCIL: doble clic en `iniciar-todo.cmd`

Explorador → carpeta `Tourinvest` → **doble clic en `iniciar-todo.cmd`**.
Se abren DOS ventanas en tu escritorio (backend `:8080` + frontend `:8081` que además
abre el navegador). Mientras uses la app, **deja ambas ventanas abiertas**; ciérralas
para detener los servidores. Sin comandos, sin rutas, sin dudas.

---

## ⚡ Arranque rápido en Windows (recomendado)

En una terminal **PowerShell** abierta en la carpeta del proyecto:

```powershell
.\setup.ps1 check         # diagnóstico completo
.\setup.ps1 seed          # carga query.sql en MySQL → crea la BD 'tourinvest' (pedirá password de root)
.\setup.ps1 run-backend   # Spring Boot  → API http://localhost:8080
.\setup.ps1 run-frontend  # sirve ./frontend → http://localhost:8081 y ABRE tu navegador
```

### ⚠️ ¿Te salió «el término '.\setup.ps1' no se reconoce...»?

Significa que **tu terminal NO está en la carpeta del proyecto** (PowerShell solo busca scripts en el directorio actual). Soluciones — cualquiera vale:

```powershell
# Opción A: entra primero a la raíz del proyecto y repite el comando
cd "C:\Users\juan2\OneDrive\Escritorio\Tourinvest"
.\setup.ps1 check

# Opción B: invócalo por ruta absoluta (desde cualquier carpeta)
& "C:\Users\juan2\OneDrive\Escritorio\Tourinvest\setup.ps1" check

# Opción C (a prueba de todo): usa los lanzadores .cmd por doble clic en el Explorador,
# o llamándolos por ruta absoluta:
& "C:\Users\juan2\OneDrive\Escritorio\Tourinvest\backend.cmd"     # arranca la API :8080
& "C:\Users\juan2\OneDrive\Escritorio\Tourinvest\frontend.cmd"    # sirve :8081 y abre el navegador
```

> 💡 **Desde VS Code**: `Terminal → Ejecutar tarea…` → elige *TourInvest: 1-check*, *2-seed*, *3-run backend* o *4-run frontend*. Las tareas usan la ruta del workspace y **nunca fallan por carpeta actual**.
>
> (Los avisos «category 'unused' ... compiler option being ignored» son ruido del análisis Java de VS Code: no afectan en nada.)

Inicia sesión en la página que se abre (`http://localhost:8081/login.html`) con los usuarios semilla de la sección 1.

---

## ❓ ¿Y ese comando «(Set-ExecutionPolicy ...) ; (& Activate.ps1)»?

Ese comando **no abre nada por diseño**: su única función es **activar el entorno virtual Python** `.venv`. Lo normal es que:

- No muestre ninguna salida ni abra ventanas (la única señal es que el prompt pasa a mostrar `(.venv)`).
- **No haga falta para abrir este proyecto**: la pila canónica (**Java `:8080` + MySQL + frontend estático `:8081`**) no usa Python. Solo se necesita si vas a correr la réplica Flask de la sección 4.

Para activarlo manualmente, usa la forma correcta (comillas y `-Force` para evitar preguntas):

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy RemoteSigned -Force
& ".\.venv\Scripts\Activate.ps1"
```

| Síntoma | Solución |
|---|---|
| «cannot be loaded because running scripts is disabled» aunque ya pusiste RemoteSigned | El archivo llegó marcado como descargado de Internet (típico al sincronizar por OneDrive). Desbloquéalo una vez: `Get-ChildItem .venv\Scripts \| Unblock-File` |
| Comandos inexistentes / error de sintaxis | Estás en **cmd.exe**, no en PowerShell. En VS Code: `Ctrl+Shift+P` → *Terminal: Select Default Profile* → **PowerShell** |
| Tras ejecutarlo no aparece nada | Es lo esperado: solo cambia el prompt a `(.venv)`. Verifica con `python --version` |
| Backend: «Public Key Retrieval is not allowed» | Ya corregido: se añadió `allowPublicKeyRetrieval=true` a la URL JDBC en `application.properties`. Vuelve a lanzar `.\setup.ps1 run-backend` |
| Backend: «Access denied for user 'root'@'localhost'» | Tecleaste mal la contraseña o cambió. Ejecuta `.\setup.ps1 set-password` y vuelve a lanzar `.\setup.ps1 run-backend` (te la volverá a pedir) |
| «El servidor responde pero Hibernate crea tablas nuevas» | Normal si aún no corriste el seed: Hibernate crea las tablas vacías. Para tener los usuarios de prueba ejecuta `.\setup.ps1 seed` una vez (⚠️ reinicia los datos de prueba) |
| «.\setup.ps1 no se reconoce» justo después de un run | Antes los comandos dejaban la terminal dentro de `backend-java`; ya está corregido (Push/Pop-Location). En sesiones ya abiertas basta `cd "C:\Users\juan2\OneDrive\Escritorio\Tourinvest"` |
| La página abre pero dice «No fue posible conectar … localhost:8080» | Falta el BACKEND: esa página vive en `:8081` pero su botón *Ingresar* llama a la API en `:8080`. Deben estar los DOS servidores corriendo a la vez (`run-backend` en una terminal y `run-frontend` en otra) |
| Login responde 403 con cuerpo vacío | Ya corregido en `SecurityConfig`: había un `StackOverflowError` por falta de `UserDetailsService` y los errores caían en `/error` no permitido (→ 403 opaco). Recompila si usas una instancia vieja |
| Login devuelve 401 «Correo o contraseña incorrectos» | Usas `juan@…` pero tras el seed el admin real es `nuevo@tourinvest.com`. O el campo JSON debe ser `contrasena` (no `password`) |
| 404 favicon.ico en la consola del servidor de frontend | Inofensivo: solo significa que no hay icono definido |
| «WARNING: A restricted method ... java.lang.System::load» (amarillo, al arrancar el backend) | **No es un error**: aviso del JDK 25 sobre Tomcat (acceso nativo). El backend funciona igual. Desaparecería con `--enable-native-access=ALL-UNNAMED` (no es necesario) |
| «Port 8080 was already in use» al lanzar el backend | Ya hay OTRO backend corriendo (¿dejaste una ventana abierta?). No lo lances dos veces; o cierra la ventana anterior |

> 💡 Para la pila Java+MySQL+frontend **ignora ese comando por completo** y sigue el arranque rápido de arriba.

---

## 0. Requisitos

| Herramienta | Version minima | Comprobar (Windows) |
|---|---|---|
| Java (JDK) | 17 | `java -version` → `17.x` |
| Maven | 3.x | `mvn -v` (si no está en PATH, ver nota abajo) |
| MySQL o MariaDB | 8+ | `mysql --version` |
| Node.js (solo validar frontend) | 18+ | `node --version` |
| Python 3 (solo la replica) | 3.10+ | `python --version` |

> **Nota Maven en Windows:** `mvn` no está en PATH en este equipo, pero existe el Maven incluido en VS Code (ruta verificada ✅):
> ```powershell
> $MVN = "C:\Users\juan2\.vscode\extensions\oracle.oracle-java-26.0.2\nbcode\java\maven\bin\mvn.cmd"
> & $MVN -v
> ```
> O agrega la carpeta `bin` de Maven al `PATH` del sistema. (`setup.ps1 check` la detecta automáticamente.)

> **Nota cliente MySQL en Windows:** `mysql` tampoco está en PATH; el cliente está en
> `C:\Program Files\MySQL\MySQL Server 9.6\bin\mysql.exe` (verificado ✅). Antes de los comandos,
> defínelo:
> ```powershell
> $MYSQL = "C:\Program Files\MySQL\MySQL Server 9.6\bin\mysql.exe"
> ```

> 🐧 Atajo Linux: `./setup.sh check` verifica todo lo anterior en un solo comando.

---

## 1. Base de datos (fuente de verdad unica)

### Windows (PowerShell)

```powershell
# 1.1 Cliente + servicio MySQL EN ESTE EQUIPO (verificados):
$MYSQL = "C:\Program Files\MySQL\MySQL Server 9.6\bin\mysql.exe"
Start-Service MySQL96        # el servicio es MySQL96 (no MySQL80); normalmente ya está Running

# 1.2 Carga el esquema + datos de prueba.
#     IMPORTANTE: query.sql vive en la RAIZ del proyecto (no en backend-java)
#     y él mismo crea/recrea la BD 'tourinvest'.
#     En PowerShell el redireccionamiento '<' no funciona; usa Get-Content:
Get-Content query.sql | & $MYSQL --default-character-set=utf8mb4 -u root -p tourinvest
#    → crea tablas y usuarios semilla (la clave real es 123456, hasheada con BCrypt):
#      ⚠️ El propio query.sql (línea ~226) RENOMBRA al administrador tras insertarlo:
#         juan@tourinvest.com pasa a ser  nuevo@tourinvest.com
#      Administrador  nuevo@tourinvest.com   / 123456
#      Analista       laura@tourinvest.com   / 123456
#      Inversionista  carlos@tourinvest.com  / 123456
#
#      ¿Prefieres recuperar juan@ como Admin? (opcional)
#      & $MYSQL -u root -p -e "USE tourinvest; UPDATE usuarios SET correo='juan@tourinvest.com' WHERE id_usuario=1;"
#
#     Alternativa automatizada:  .\setup.ps1 seed   (detecta mysql.exe solo)

# 1.3 Verifica vocabulario coherente (los ENUM son la referencia)
& $MYSQL -u root -p -e "USE tourinvest; SELECT id_usuario, correo, estado FROM usuarios;"
```

### 🐧 Linux/macOS

```bash
# 1.1 Inicia MySQL
sudo service mysql start          # o: sudo mysqld_safe --user=mysql &

# 1.2 (opcional) crea la base y/o el usuario
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS tourinvest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 1.3 Carga el esquema + datos de prueba
mysql --default-character-set=utf8mb4 -u root -p tourinvest < query.sql

# 1.4 Verifica vocabulario coherente
mysql -u root -p -e "USE tourinvest; SELECT id_usuario, correo, estado FROM usuarios;"
```

--- 

## 2. Backend Java Spring Boot (API REST canónica + JWT) — `:8080`

### Windows (PowerShell)

```powershell
cd backend-java

# 2.1 Contraseña MySQL — AUTOMÁTICO con setup.ps1
#     `.\setup.ps1 run-backend` detecta que la contraseña no está configurada,
#     TE LA PIDE UNA VEZ (oculta), la guarda aquí y, si la BD 'tourinvest' aún
#     no existe, carga query.sql automáticamente (usuarios semilla clave 123456).
#
#     ¿Prefieres hacerlo a mano? Edita src/main/resources/application.properties
#     y cambia la línea LITERAL  spring.datasource.password=TU_PASSWORD
#     por tu contraseña real (o déjala vacía tras '=' si root no tiene).
(Get-Content src\main\resources\application.properties) -replace '^spring\.datasource\.password=.*$', 'spring.datasource.password=TU_PASSWORD_REAL_DE_ROOT' | Set-Content src\main\resources\application.properties

# 2.2 Compila (solo producción)
#     Si 'mvn' no está en PATH, usa la ruta completa:
$MVN = "C:\Users\juan2\.vscode\extensions\oracle.oracle-java-26.0.2\nbcode\java\maven\bin\mvn.cmd"
& $MVN -q -DskipTests clean install

# 2.3 Ejecuta
& $MVN spring-boot:run
#   → escucha en http://localhost:8080
```

### 🐧 Linux/macOS

```bash
cd backend-java

# 2.1 Ajusta credenciales MySQL si root tiene password:
sed -i 's|^spring.datasource.password=.*|spring.datasource.password=TU_PASSWORD_REAL_DE_ROOT|' src/main/resources/application.properties

# 2.2 Compila (solo producción)
mvn -q -DskipTests clean install

# 2.3 Ejecuta
mvn spring-boot:run
#   → escucha en http://localhost:8080
```

### Contrato de auth (el frontend ya lo respeta)
- `POST /auth/registro`  body `{nombre1, apellido1, cedula, fechaNacimiento, correo, contrasena, confirmarContrasena}` → siempre rol Inversionista (RF04).
- `POST /auth/login`     body `{correo, contrasena}` → `{token, nombre1, correo, rol}`.
- `POST /auth/recuperar` body `{correo}` → `{mensaje}`.
- Token JWT en header `Authorization: Bearer <token>`. 401 → el frontend limpia `sessionStorage` y vuelve a `login.html`.

### Endpoints canonicos (los `dashboard_*.js` ya los consumen)
| Recurso | Metodo | Ruta | Body (si aplica) |
|---|---|---|---|
| Auth | POST | `/auth/{login,registro,recuperar}` | login/registro |
| Resumen portafolio | GET | `/inversionista/resumen` | — |
| Agregar inversion | POST | `/inversionista/portafolio/inversiones` | `{idAccion, cantidad}` |
| Empresas (admin) | POST/PUT/DELETE | `/empresas[/{id}]` | `{nombre, simbolo, sector, pais, precio}` (solo Administrador; consulta con GET para cualquier autenticado) |
| Alertas | GET/POST | `/inversionista/alertas` | `{idAccion, precioObjetivo}` |
| Cancelar alerta | PATCH | `/inversionista/alertas/{id}/cancelar` | — |
| Empresas | GET | `/empresas` | — |
| Reportes (analista) | GET/POST | `/analista/reportes` | POST `{idEmpresa, titulo, descripcion}` |
| Liquidez corriente | POST | `/analista/indicadores/liquidez` | `{activoCorriente, pasivoCorriente}` → `{liquidezCorriente}` |
| Usuarios (admin) | GET | `/admin/usuarios` | — |
| Suspender/activar (admin) | PATCH | `/admin/usuarios/{id}/{suspender,activar}` | — |

> 💡 **CORS**: `SecurityConfig` permite orígenes `:5500` (Live Server) y `:8081` (dev estático), métodos GET/POST/PUT/PATCH/DELETE/OPTIONS. Si sirves el frontend en otro puerto, agrégalo a `setAllowedOrigins(...)`.

> ⚠️ Si `pasivoCorriente = 0` el servicio lanza `ArithmeticException`, que `GlobalExceptionHandler` mapea a **HTTP 400** (validado: `Errors: 0`).

---

## 3. Frontend (HTML/CSS/JS estático)

No necesita proceso propio; es estático y llama (CORS) al backend Java `:8080`.

### Windows (PowerShell)

```powershell
cd frontend

# Opcional: valida la sintaxis de los JS unificados / dashboards
node --check js\auth.js
node --check js\dashboard.js
node --check js\dashboard_administrador.js
node --check js\dashboard_analista.js

# Opcion rapida de desarrollo (sirve cualquier .html)
python -m http.server 8081   # http://localhost:8081/login.html
```

### 🐧 Linux/macOS

```bash
cd frontend

# Opcional: valida la sintaxis de los JS unificados / dashboards
node --check js/auth.js
node --check js/dashboard.js
node --check js/dashboard_administrador.js
node --check js/dashboard_analista.js

# Opcion rapida de desarrollo (sirve cualquier .html)
python3 -m http.server 8081   # http://localhost:8081/login.html
```

### Estado de los dashboards y enlaces
- ✅ `js/auth.js` cableado y coherente: login/registro/recuperar → `:8080`, token en `sessionStorage`, redirect por rol a `administrador.html` / `analista.html` / `inversionista.html`.
- ✅ Se creó `frontend/index.html` (la ruta a la que apuntaban los botones "home" de los dashboards; ahora existe).
- ✅ `css/styles.css` alineado con el vocabulario que usan auth y dashboards (`.field`, `.mensaje-global`, `.sidebar__link`, `.vista`, tablas, modales, toasts).
- ✅ **Wiring completo (fase D1/D2 de TODO.md):** los 3 dashboards consumen la API Java `:8080` en vivo (vistas `data-vista`/`data-vista-panel`, resumen/portafolio, alertas, empresas, indicadores, reportes, usuarios con buscador, roles/auditorías).
- ✅ Vista **Noticias** añadida al inversionista (mockup 6.3) y **buscador** en Gestión de Usuarios (mockup 6.8).
- ✅ Modal de detalle de empresa operativo (cierra con ✕, fondo o ESC) y sin doble envío de formularios.
- ✅ `SecurityConfig` con `UserDetailsService` real (login contra MySQL/BCrypt) y `/error` permitido (adiós al 403 opaco por StackOverflow).

---

## 4. Replica Python (opcional — no la usa el frontend)

### Windows (PowerShell)

```powershell
# Desde la RAIZ del proyecto. Este es el UNICO flujo que necesita el .venv;
# la pila canonica (Java + MySQL + frontend) NO requiere activarlo.
Set-ExecutionPolicy -Scope Process -ExecutionPolicy RemoteSigned -Force
& ".\.venv\Scripts\Activate.ps1"          # el prompt cambiará a (.venv)

pip install -r backend-python\requirements.txt
Set-Location backend-python
python app.py          # http://localhost:5000/api/health (réplica del modelo; vocabulario coherente con MySQL)
```

### 🐧 Linux/macOS

```bash
cd backend-python
pip install -r requirements.txt
python3 app.py          # http://localhost:5000 (réplica del modelo; vocabulario coherente con MySQL)
```

---

## 5. Tests Java

### Windows (PowerShell)

```powershell
cd backend-java
# Si 'mvn' no está en PATH, usa la ruta completa:
$MVN = "C:\Users\juan2\.vscode\extensions\oracle.oracle-java-26.0.2\nbcode\java\maven\bin\mvn.cmd"
& $MVN test                # Tests run: 74, Failures: 0, Errors: 38 (ver nota)
```

### 🐧 Linux/macOS

```bash
cd backend-java
mvn test                # Tests run: 74, Failures: 0, Errors: 38 (ver nota)
```

> ⚠️ **Nota sobre los 38 errores:** son `MockitoException: Mockito cannot mock this class` — incompatibilidad de Mockito con **Java 25** (el entorno ejecuta con Java 25 aunque el proyecto compila con `release 17`). No son fallos de assertion ni incoherencias de producto. Para que pasen, ejecuta con un JDK 17 (p. ej. `JAVA_HOME` apuntando a un JDK 17) o actualiza la versión de Mockito/ByteBuddy en `pom.xml`.

---

## 6. Smoke test (confirma coherencia en vivo)

### Windows (PowerShell)

> ⚠️ **El campo de contraseña se llama `contrasena`** (sin ñ) en el JSON del login,
> tal como espera `LoginRequest.java`. Y TODOS los endpoints (incluido `/empresas`)
> exigen `Authorization: Bearer <token>` porque `anyRequest().authenticated()`.

```powershell
# 6.1 Login como analista de la seed
$resp = Invoke-RestMethod -Method Post -Uri http://localhost:8080/auth/login `
  -ContentType 'application/json' `
  -Body '{"correo":"laura@tourinvest.com","contrasena":"123456"}'
$TOKEN = $resp.token
Write-Host "rol=$($resp.rol)  token=$TOKEN"

# 6.2 Llama a endpoints reales con el token SIEMPRE en la cabecera
Invoke-RestMethod -Uri http://localhost:8080/empresas -Headers @{ Authorization = "Bearer $TOKEN" } | ConvertTo-Json -Depth 5
Invoke-RestMethod -Uri http://localhost:8080/inversionista/resumen -Headers @{ Authorization = "Bearer $TOKEN" } | ConvertTo-Json -Depth 5

# 6.3 Login como administrador (query.sql deja su correo como nuevo@...)
Invoke-RestMethod -Method Post -Uri http://localhost:8080/auth/login `
  -ContentType 'application/json' `
  -Body '{"correo":"nuevo@tourinvest.com","contrasena":"123456"}'
```

### 🐧 Linux/macOS

```bash
# 6.1 Login como analista de la seed (el campo es "contrasena")
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"correo":"laura@tourinvest.com","contrasena":"123456"}' \
  | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')
echo "token=$TOKEN"

# 6.2 Llama a endpoints reales (siempre con Bearer token)
curl -s http://localhost:8080/empresas -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
curl -s http://localhost:8080/inversionista/resumen -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

---

## 7. Orden de arranque recomendado

1. Base de datos: `.\setup.ps1 seed` (carga `query.sql` de la raíz; el servicio `MySQL96` ya corre en Windows).
2. Backend: `.\setup.ps1 run-backend` → API REST + JWT en `http://localhost:8080`.
3. Frontend: `.\setup.ps1 run-frontend` → sirve `:8081` y abre automáticamente `http://localhost:8081/login.html` en tu navegador.
4. Login con los correos de la seed (sección 1): todos con clave `123456`.
5. (Opcional) Réplica Python: sección 4 → `http://localhost:5000/api/health`.

🐧 Linux/macOS equivalente: `./setup.sh check | seed | run-backend | run-frontend` desde la raíz.