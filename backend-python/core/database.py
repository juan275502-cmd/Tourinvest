"""
core/database.py
Capa 'core' compartida entre features (ver arquitectura.md -> core / shared).
Simula la base de datos MySQL mencionada en README.md mediante estructuras en
memoria, para que el proyecto pueda ejecutarse sin necesidad de un servidor
MySQL real. Los valores de estado (Activo/Inactivo) y roles
(Administrador/Analista/Inversionista) coinciden con los ENUM de MySQL definidos
en query.sql, de modo que la réplica en memoria es vocabulariamente coherente con
el backend Java + MySQL. En producción, este módulo se reemplazaría por
conexiones mysql-connector-python / SQLAlchemy hacia MySQL.
"""

USUARIOS_DB = [
    {"id": 1, "nombre": "Juan", "apellido": "Pérez", "cedula": "1001001001",
     "correo": "inversor@tourinvest.com", "password": "inversor123",       "rol": "Inversionista", "estado": "Activo"},
    {"id": 2, "nombre": "María", "apellido": "Gómez", "cedula": "1002002002",
     "correo": "analista@tourinvest.com", "password": "analista123",       "rol": "Analista", "estado": "Activo"},
    {"id": 3, "nombre": "Carlos", "apellido": "Ruiz", "cedula": "1003003003",
     "correo": "admin@tourinvest.com", "password": "admin123",       "rol": "Administrador", "estado": "Activo"},
]

EMPRESAS_DB = [
    {"id": 1, "nombre": "EcoTravel S.A.", "sector": "Turismo"},
    {"id": 2, "nombre": "Andes Capital", "sector": "Financiero"},
    {"id": 3, "nombre": "InverTech", "sector": "Tecnología"},
    {"id": 4, "nombre": "Global Hoteles", "sector": "Hotelería"},
    {"id": 5, "nombre": "Rutas del Sur", "sector": "Transporte"},
]

PORTAFOLIO_DB = [
    {"empresa": "EcoTravel S.A.", "participacion": 28, "rendimiento": 4.5},
    {"empresa": "Andes Capital", "participacion": 22, "rendimiento": 1.8},
    {"empresa": "InverTech", "participacion": 19, "rendimiento": -0.6},
    {"empresa": "Global Hoteles", "participacion": 17, "rendimiento": 2.1},
    {"empresa": "Rutas del Sur", "participacion": 14, "rendimiento": -1.2},
]

ALERTAS_DB = [
    {"empresa": "InverTech", "descripcion": "Caída de indicador de liquidez", "prioridad": "alta"},
    {"empresa": "Rutas del Sur", "descripcion": "Variación inusual en el precio", "prioridad": "media"},
    {"empresa": "Andes Capital", "descripcion": "Nuevo reporte financiero disponible", "prioridad": "baja"},
]

INDICADORES_DB = [
    {"indicador": "Liquidez corriente", "empresa": "InverTech", "valor": 1.2},
    {"indicador": "ROE", "empresa": "EcoTravel S.A.", "valor": 14.5},
    {"indicador": "Endeudamiento", "empresa": "Rutas del Sur", "valor": 62},
]

REPORTES_DB = [
    {"titulo": "Análisis trimestral Q2", "empresa": "EcoTravel S.A.", "fecha": "2026-06-30"},
    {"titulo": "Evaluación de riesgo", "empresa": "Rutas del Sur", "fecha": "2026-06-15"},
]

AUDITORIAS_DB = [
    {"fecha": "2026-07-05", "usuario": "María Gómez", "accion": "Creó un nuevo análisis"},
    {"fecha": "2026-07-04", "usuario": "Carlos Ruiz", "accion": "Suspendió al usuario Luis Torres"},
]


def siguiente_id(coleccion):
    return max([item["id"] for item in coleccion], default=0) + 1
