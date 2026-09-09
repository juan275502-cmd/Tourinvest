from flask import Blueprint, request, jsonify
from features.analysis.data.repository_impl import AnalysisRepositoryImpl

analysis_bp = Blueprint("analysis", __name__, url_prefix="/api/analysis")
repo = AnalysisRepositoryImpl()


@analysis_bp.route("/empresas", methods=["GET"])
def empresas():
    return jsonify({"ok": True, "empresas": repo.obtener_empresas()})


@analysis_bp.route("/indicadores", methods=["GET"])
def indicadores():
    return jsonify({"ok": True, "indicadores": repo.obtener_indicadores()})


@analysis_bp.route("/reportes", methods=["GET"])
def reportes():
    return jsonify({"ok": True, "reportes": repo.obtener_reportes()})


@analysis_bp.route("/reportes", methods=["POST"])
def crear_reporte():
    """RF10 - Crear análisis"""
    data = request.get_json(silent=True) or {}
    if not data.get("empresa"):
        return jsonify({"ok": False, "mensaje": "Debe indicar la empresa a analizar."}), 400

    nuevo = repo.crear_analisis(data)
    return jsonify({"ok": True, "reporte": nuevo}), 201
