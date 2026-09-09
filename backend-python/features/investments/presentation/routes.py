from flask import Blueprint, jsonify
from features.investments.data.repository_impl import InvestmentsRepositoryImpl

investments_bp = Blueprint("investments", __name__, url_prefix="/api/investments")
repo = InvestmentsRepositoryImpl()


@investments_bp.route("/portafolio", methods=["GET"])
def portafolio():
    """RF07 - Mostrar portafolio"""
    return jsonify({
        "ok": True,
        "valor_total": repo.valor_total_portafolio(),
        "posiciones": repo.obtener_portafolio(),
    })


@investments_bp.route("/alertas", methods=["GET"])
def alertas():
    return jsonify({"ok": True, "alertas": repo.obtener_alertas()})


@investments_bp.route("/empresas", methods=["GET"])
def empresas():
    return jsonify({"ok": True, "empresas": repo.obtener_empresas()})
