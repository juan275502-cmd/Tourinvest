"""
TourInvest - Backend REST API (Python / Flask)
Arquitectura Clean Architecture + Features First (ver arquitectura.md)

Estructura:
  core/                       -> utilidades y datos compartidos
  features/authentication/    -> login, registro (RF01, RF04)
  features/investments/       -> portafolio, alertas, empresas (RF07)
  features/analysis/          -> empresas, indicadores, reportes (RF10)
  features/dashboard/         -> usuarios, roles, auditorías (RF13)

Ejecutar:
  pip install -r requirements.txt
  python app.py
  -> API disponible en http://localhost:5000/api/...
"""

from flask import Flask, jsonify

from features.authentication.presentation.routes import auth_bp
from features.investments.presentation.routes import investments_bp
from features.analysis.presentation.routes import analysis_bp
from features.dashboard.presentation.routes import dashboard_bp


def create_app():
    app = Flask(__name__)

    # Habilita CORS para que el frontend (HTML/CSS) pueda consumir la API
    # desde otro origen. Se usa flask-cors si está instalado; si no,
    # se agregan las cabeceras manualmente como alternativa sin dependencias.
    try:
        from flask_cors import CORS
        CORS(app)
    except ImportError:
        @app.after_request
        def add_cors_headers(response):
            response.headers["Access-Control-Allow-Origin"] = "*"
            response.headers["Access-Control-Allow-Headers"] = "Content-Type"
            response.headers["Access-Control-Allow-Methods"] = "GET,POST,PATCH,OPTIONS"
            return response

    app.register_blueprint(auth_bp)
    app.register_blueprint(investments_bp)
    app.register_blueprint(analysis_bp)
    app.register_blueprint(dashboard_bp)

    @app.route("/api/health", methods=["GET"])
    def health():
                return jsonify({"ok": True, "servicio": "TourInvest API", "estado": "Activo"})

    return app


app = create_app()

if __name__ == "__main__":
    app.run(debug=True, port=5000)
