# -*- coding: utf-8 -*-
"""
TourInvest - Tests de la réplica Python (backend-python).

Estos tests usan UNICAMENTE la biblioteca estandar `unittest`, de modo que se
pueden ejecutar sin instalar dependencias adicionales:

   desde la raiz del proyecto:
   python -m unittest discover -s backend-python/tests -t backend-python/tests

o bien, si tienes pytest instalado:
   pytest backend-python/tests -q

Contenido:
  - test_repositories: unit tests de las implementaciones en memoria
    (autenticacion, registro, analisis, invesiones).
  - test_api: tests de integracion sobre el Flask test-client. Si Flask no esta
    instalado en el entorno actual, estos N se ejecutan y se marcan como skipped
    con un mensaje claro, sin romper la suite.

Nota de vocabulario: los fixtures de core/database.py estan alineados con
MySQL/Java (estado Activo/Inactivo, roles Administrador/Analista/Inversionista).
"""