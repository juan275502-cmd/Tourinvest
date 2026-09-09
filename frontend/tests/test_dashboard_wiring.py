# -*- coding: utf-8 -*-
"""Suite de validacion de estructura HTML/JS de los dashboards de TourInvest."""

import os
import re
import unittest

FRONTEND = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))  # .../frontend
JS_DIR = os.path.join(FRONTEND, "js")
HTML_DIR = FRONTEND


def leer(ruta):
    with open(ruta, encoding="utf-8") as f:
        return f.read()


def listar_ids_driver(js):
    return set(re.findall(r'getElementById\("([^"]+)"\)', js))


def listar_campos_form(js):
    return set(re.findall(r"formulario\.([A-Za-z_][A-Za-z0-9_]*)\.value", js))


DASHBOARDS = {
    "inversionista.html": "dashboard.js",
    "analista.html": "dashboard_analista.js",
    "administrador.html": "dashboard_administrador.js",
}


class TestDashboardScriptsCargados(unittest.TestCase):
    def test_cada_dashboard_carga_su_driver(self):
        for html, driver in DASHBOARDS.items():
            html_src = leer(os.path.join(HTML_DIR, html))
            self.assertIn(f'src="js/{driver}"', html_src,
                          f"{html} debe cargar js/{driver}")


class TestDashboardIds(unittest.TestCase):
    def test_ids_del_driver_presentes(self):
        for html, driver in DASHBOARDS.items():
            html_src = leer(os.path.join(HTML_DIR, html))
            js_src = leer(os.path.join(JS_DIR, driver))
            ids_necesarios = listar_ids_driver(js_src)
            for id_ in ids_necesarios:
                with self.subTest(html=html, id=id_):
                    self.assertIn(f'id="{id_}"', html_src,
                                  f"{html} no contiene el id '{id_}' requerido por {driver}")

    def test_vistas_panel_por_dashboard(self):
        esperado = {
            "inversionista.html": ["portafolio", "alertas", "empresas", "perfil"],
            "analista.html": ["empresas", "indicadores", "reportes", "recomendaciones", "perfil"],
            "administrador.html": ["usuarios", "roles", "auditorias", "configuracion"],
        }
        for html, panes in esperado.items():
            html_src = leer(os.path.join(HTML_DIR, html))
            for p in panes:
                with self.subTest(html=html, panel=p):
                    self.assertIn(f'data-vista-panel="{p}"', html_src,
                                  f"{html} no contiene la vista '{p}'")


class TestFormulariosCableados(unittest.TestCase):
    """Los formularios que el driver enlaza por id deben tener los campos que lee."""

    CASOS = {
        "inversionista.html": {
            "form-agregar-portafolio": ["cantidad"],
            "form-crear-alerta": ["precioObjetivo"],
        },
        "analista.html": {
            "form-liquidez": ["activoCorriente", "pasivoCorriente"],
            "form-crear-reporte": ["idEmpresa", "titulo", "descripcion"],
        },
    }

    def _bloque_form(self, html, form_id):
        m = re.search(r'<form[^>]*id="%s".*?</form>' % form_id, html, re.S)
        self.assertIsNotNone(m, f"No se encontro el form {form_id}")
        return m.group(0)

    def test_campos_requeridos_presentes(self):
        for html, forms in self.CASOS.items():
            html_src = leer(os.path.join(HTML_DIR, html))
            for form_id, campos in forms.items():
                bloque = self._bloque_form(html_src, form_id)
                for campo in campos:
                    with self.subTest(html=html, form=form_id, campo=campo):
                        self.assertIn(f'name="{campo}"', bloque,
                                      f"El form {form_id} de {html} deberia tener name='{campo}'")


class TestSinResiduosDeVocabularioViejo(unittest.TestCase):
    def test_no_hay_campo(self):
        patron = re.compile(r'\.campo__|\.campo--|closest\("\.campo"\)')
        for a in os.listdir(JS_DIR):
            if a.endswith(".js"):
                self.assertFalse(patron.search(leer(os.path.join(JS_DIR, a))),
                                 f"Residuo de vocabulario .campo en js/{a}")


class TestAuthCableado(unittest.TestCase):
    def test_auth_carga_auth_js(self):
        for pagina in ["login.html", "registro.html", "recuperar.html"]:
            src = leer(os.path.join(HTML_DIR, pagina))
            self.assertIn('src="js/auth.js"', src, f"{pagina} no carga js/auth.js")

    def test_campos_login(self):
        src = leer(os.path.join(HTML_DIR, "login.html"))
        for campo in ["correo", "contrasena"]:
            self.assertIn(f'name="{campo}"', src)

    def test_campos_registro(self):
        src = leer(os.path.join(HTML_DIR, "registro.html"))
        for campo in ["nombre1", "apellido1", "cedula", "fechaNacimiento", "correo",
                      "contrasena", "confirmarContrasena"]:
            self.assertIn(f'name="{campo}"', src)


if __name__ == "__main__":
    unittest.main()