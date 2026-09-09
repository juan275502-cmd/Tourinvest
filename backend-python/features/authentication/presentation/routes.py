"""
Presentation / Routes
Expone los casos de uso de authentication como endpoints REST
(ver arquitectura.md -> Presentation: Screens/Providers -> aquí, API endpoints).
"""

from flask import Blueprint, request, jsonify
from features.authentication.data.repository_impl import UsuarioRepositoryImpl

auth_bp = Blueprint("authentication", __name__, url_prefix="/api/authentication")
repo = UsuarioRepositoryImpl()


@auth_bp.route("/login", methods=["POST"])
def login():
    """RF01 - Autenticar usuario"""
    data = request.get_json(silent=True) or {}
    correo = data.get("correo", "")
    password = data.get("password", "")

    usuario = repo.autenticar(correo, password)
    if usuario is None:
        return jsonify({"ok": False, "mensaje": "Correo o contraseña incorrectos."}), 401

    return jsonify({"ok": True, "usuario": usuario.to_dict()}), 200


@auth_bp.route("/registro", methods=["POST"])
def registro():
    """RF04 - Registrar usuario"""
    data = request.get_json(silent=True) or {}
    campos_requeridos = ["nombre", "apellido", "cedula", "correo", "password"]

    faltantes = [c for c in campos_requeridos if not data.get(c)]
    if faltantes:
        return jsonify({"ok": False, "mensaje": f"Campos faltantes: {', '.join(faltantes)}"}), 400

    try:
        usuario = repo.registrar(data)
    except ValueError as e:
        return jsonify({"ok": False, "mensaje": str(e)}), 409

    return jsonify({"ok": True, "usuario": usuario.to_dict()}), 201


@auth_bp.route("/recuperar", methods=["POST"])
def recuperar():
    """Recuperar contraseña (en un entorno real se enviaría un email)."""
    data = request.get_json(silent=True) or {}
    correo = data.get("correo", "").strip().lower()

    if not correo:
        return jsonify({"ok": False, "mensaje": "Debe indicar el correo."}), 400

    # Por seguridad, no se revela si el correo existe o no:
    # simplemente se responde que se enviarán las instrucciones si está registrado.
    return jsonify({"ok": True, "mensaje": "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña."}), 200
