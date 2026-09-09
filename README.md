# TourInvest — Implementación en HTML, CSS, Python y Java

Este proyecto traduce la documentación de TourInvest Mobile (originalmente en
Flutter/Dart) a una pila **web + backend Python + núcleo de dominio en Java**,
conservando la arquitectura, navegación, roles y prototipos definidos en los
documentos fuente (`arquitectura.md`, `navegacion.md`, `prototipos.md`,
`usabilidad_accesibilidad.md`, `xml_android.md`, `layouts_android.md`,
`maquetacion_android_ev08.md`, `README.md`).

## Estructura del proyecto

```
tourinvest/
├── frontend/                     # HTML + CSS (Presentation)
│   ├── css/styles.css            # Paleta: #1E3A5F, #2E8B57, #FFFFFF + responsive
│   ├── login.html                # RF01 - Autenticar usuario
│   ├── registro.html             # RF04 - Registrar usuario
│   ├── recuperar.html            # Recuperar / restablecer contraseña
│   ├── inversionista.html        # RF07 - Portafolio, Alertas, Noticias, Empresas, Perfil
│   ├── analista.html             # RF10 - Empresas, Indicadores, Reportes, Recomendaciones
│   └── administrador.html        # RF13 - Usuarios, Roles, Auditorías, Configuración
│
├── backend-python/               # API REST (Clean Architecture + Features First)
│   ├── app.py                    # Punto de entrada Flask
│   ├── requirements.txt
│   ├── core/database.py          # Datos compartidos (simula MySQL de README.md)
│   └── features/
│       ├── authentication/{domain,data,presentation}   # login, registro
│       ├── investments/{domain,data,presentation}      # portafolio, alertas, empresas
│       ├── analysis/{domain,data,presentation}         # indicadores, reportes
│       └── dashboard/presentation                      # usuarios, roles, auditorías
│
└── backend-java/                 # Núcleo de dominio (Entities + Use Cases)
    └── src/main/java/com/tourinvest/
        ├── domain/entities/      # Usuario, Empresa, PosicionPortafolio, Alerta, Reporte
        ├── domain/repositories/  # Contratos (interfaces)
        ├── domain/usecases/      # AutenticarUsuario, RegistrarUsuario, GestionarUsuarios, CrearAnalisis
        ├── data/                 # Implementación en memoria del repositorio
        └── Main.java             # Demo ejecutable de la capa de dominio
```

## Cómo se mapea cada documento

| Documento | Dónde se refleja |
|---|---|
| `arquitectura.md` | Carpetas `domain/`, `data/`, `presentation/` en Python y Java; `core/` compartido |
| `navegacion.md` | Menús laterales (`sidebar`) de cada dashboard HTML, con las mismas secciones por rol |
| `prototipos.md` | Componentes exactos de cada pantalla (Login, Registro, 3 dashboards) y sus RF |
| `usabilidad_accesibilidad.md` | Alto contraste, tipografía legible, `@media` responsive, validaciones de formulario |
| `README.md` (original) | Paleta de colores, roles y tecnologías (Python, Java, REST API) |
| `xml_android.md` / `layouts_android.md` | Inspiraron los formularios HTML (mismos campos: email, password, nombre, apellido, cédula) |

## Cómo ejecutar

### 1. Frontend (HTML/CSS)
El frontend (HTML/CSS/JS) consume la API Spring Boot de Java (`http://localhost:8080`) con JWT
y persiste el token en `sessionStorage`. Ábrelo directamente en el navegador
(`frontend/login.html`); también puedes abrir `frontend/index.html` (landing).

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | juan@tourinvest.com | 123456 |
| Analista | laura@tourinvest.com | 123456 |
| Inversionista | carlos@tourinvest.com | 123456 |

### 2. Backend Java Spring Boot (API REST canónica — :8080)
Requiere MySQL corriendo y la base `tourinvest` (`query.sql`):
```bash
cd backend-java
# 1) crear la base y el seed de desarrollo:
mysql -u root < query.sql
# 2) arrancar la API Spring Boot (+ JWT + MySQL) en :8080
mvn spring-boot:run
```
Contrato único de la API (lo consumen los dashboards y las páginas de auth):
- `POST /auth/login`      → `{ token, nombre1, correo, rol }` (200)
- `POST /auth/registro`   → `RegistroRequest` → `UsuarioResumenDTO` (201)
- `POST /auth/recuperar`  → `{ mensaje }` (200)
- `GET  /inversionista/resumen`, `POST /inversionista/portafolio/inversiones`,
  `GET /inversionista/alertas`, `PATCH /inversionista/alertas/{id}/cancelar`
- `GET /empresas`, `GET /empresas/{id}`
- `GET /analista/reportes`, `POST /analista/reportes`, `POST /analista/indicadores/liquidez`
- `GET /admin/usuarios`, `PATCH /admin/usuarios/{id}/suspender`, `PATCH /admin/usuarios/{id}/activar`

### 3. Núcleo de dominio Java (Clean Architecture — demo)
Capa pura sin Spring/JPA/JWT (reglas de negocio reutilizables):
```bash
cd backend-java
javac -d out \
  $(find src/main/java/com/tourinvest/domain -name "*.java") \
  $(find src/main/java/com/tourinvest/data -name "*.java") \
  src/main/java/com/tourinvest/Main.java
java -cp out com.tourinvest.Main
```
Ejecuta una demo de los casos de uso (autenticación, registro, gestión de
usuarios y creación de análisis) sobre las entidades de dominio, independiente
del framework web.

### 4. Backend Python (réplica coherente — :5000)
Réplica del vocabulario y del modelo de datos sobre MySQL; útil para pruebas
unitarias del dominio `features/` sin levantar Spring. Usa datos en memoria
(`core/database.py`) con el mismo vocabulario que MySQL:
```bash
cd backend-python
pip install -r requirements.txt
python app.py
# http://localhost:5000/api/health
```

## Notas de diseño (coherencia)

- **Una sola pila canónica:** el frontend y los dashboards consumen el backend
  Spring Boot de Java (`:8080`) sobre **MySQL** (`tourinvest`) con **JWT**.
- **Backend único de datos:** `backend-java/src/main/resources/application.properties`
  apunta a `jdbc:mysql://localhost:3306/tourinvest` y `query.sql` es el esquema
  fuente de verdad. El vocabulario (`EstadoUsuario.Activo/Inactivo`,
  `Rol.NombreRol.Administrador/Analista/Inversionista`) coincide en Java, MySQL
  y la réplica Python.
- **Contrato único de autenticación:** `POST /auth/login` devuelve
  `{ token, nombre1, correo, rol }`; el frontend persiste el token en
  `sessionStorage` y lo envía como `Authorization: Bearer <token>`.
- **Réplica Python (`:5000`):** `backend-python` mantiene el mismo vocabulario y
  modelo de datos que MySQL (`core/database.py`), de modo que sigue
  ejecutándose sin MySQL real; en producción su capa `data` se sustituye por
  conexiones a MySQL.
- Clean Architecture (`domain/`, `data/`, `features/`) se mantiene expresada en
  Python y en Java; el backend Java Spring reúne controllers/services/security.
