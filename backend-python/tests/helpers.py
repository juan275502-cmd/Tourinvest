# -*- coding: utf-8 -*-
"""Helpers de los tests de backend-python."""

import importlib
import os
import sys


def cargar_modulo(modulo):
    """Importa un modulo de backend-python asegurandose de que su raiz este en sys.path."""
    raiz = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))  # /backend-python
    if raiz not in sys.path:
        sys.path.insert(0, raiz)
    return importlib.import_module(modulo)


def flask_disponible():
    """Devuelve True si el entorno actual puede importar Flask."""
    try:
        import flask  # noqa
        return True
    except Exception:
        return False