"""
Domain / Repository Contract
Define el contrato que debe cumplir cualquier implementación de datos
(ver arquitectura.md -> Domain: Repository Contracts).
"""

from abc import ABC, abstractmethod


class UsuarioRepository(ABC):

    @abstractmethod
    def autenticar(self, correo: str, password: str):
        """Retorna el usuario si las credenciales son válidas (RF01)."""
        raise NotImplementedError

    @abstractmethod
    def registrar(self, datos: dict):
        """Registra un nuevo inversionista (RF04)."""
        raise NotImplementedError
