"""
Domain / Entities
Refleja la capa Domain descrita en arquitectura.md (Entities, Use Cases,
Repository Contracts) para el feature 'authentication'.
"""

from dataclasses import dataclass


@dataclass
class Usuario:
    id: int
    nombre: str
    apellido: str
    cedula: str
    correo: str
    rol: str          # administrador | analista | inversionista
    estado: str = "activo"

    def to_dict(self):
        return {
            "id": self.id,
            "nombre": self.nombre,
            "apellido": self.apellido,
            "cedula": self.cedula,
            "correo": self.correo,
            "rol": self.rol,
            "estado": self.estado,
        }
