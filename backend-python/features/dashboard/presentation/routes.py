"""
Feature 'dashboard' (rol Administrador): gestión de usuarios, roles y auditorías.
Ver navegacion.md -> Administrador: Usuarios, Roles, Auditorías, Configuración.
"""

from flask import Blueprint, request, jsonify
from core.database import USUARIOS_DB, AUDITORIAS_DB, siguiente_id

dashboard_bp = Blueprint("dashboard", __name__, url_prefix="/api/dashboard")

ROLES = [
    {"rol": "Administrador", "permisos": "Gestionar usuarios, roles y auditorías"},
    {"rol": "Analista", "permisos": "Gestionar empresas, crear análisis, generar reportes"},
    {"rol": "Inversionista", "permisos": "Consultar portafolio, alertas y noticias"},
]


@dashboard_bp.route("/usuarios", methods=["GET"])
def listar_usuarios():
    return jsonify({"ok": True, "usuarios": [
        {k: v for k, v in u.items() if k != "password"} for u in USUARIOS_DB
    ]})


@dashboard_bp.route("/usuarios", methods=["POST"])
def crear_usuario():
    """RF13 - Gestionar usuarios"""
    data = request.get_json(silent=True) or {}
    requeridos = ["nombre", "apellido", "cedula", "correo", "password", "rol"]
    faltantes = [c for c in requeridos if not data.get(c)]
    if faltantes:
        return jsonify({"ok": False, "mensaje": f"Campos faltantes: {', '.join(faltantes)}"}), 400

    nuevo = {**data, "id": siguiente_id(USUARIOS_DB), "estado": "Activo"}
    USUARIOS_DB.append(nuevo)
    return jsonify({"ok": True, "usuario": {k: v for k, v in nuevo.items() if k != "password"}}), 201


@dashboard_bp.route("/usuarios/<int:usuario_id>/estado", methods=["PATCH"])
def cambiar_estado_usuario(usuario_id):
    """RF13 - Suspender / activar usuario"""
    data = request.get_json(silent=True) or {}
    nuevo_estado = data.get("estado")
    if nuevo_estado not in ("Activo", "Inactivo"):
        return jsonify({"ok": False, "mensaje": "Estado inválido."}), 400

    for u in USUARIOS_DB:
        if u["id"] == usuario_id:
            u["estado"] = nuevo_estado
            return jsonify({"ok": True, "usuario": {k: v for k, v in u.items() if k != "password"}})

    return jsonify({"ok": False, "mensaje": "Usuario no encontrado."}), 404


@dashboard_bp.route("/roles", methods=["GET"])
def listar_roles():
    return jsonify({"ok": True, "roles": ROLES})


@dashboard_bp.route("/auditorias", methods=["GET"])
def listar_auditorias():
    return jsonify({"ok": True, "auditorias": AUDITORIAS_DB})
