from dataclasses import dataclass


@dataclass
class PosicionPortafolio:
    empresa: str
    participacion: float
    rendimiento: float


@dataclass
class Alerta:
    empresa: str
    descripcion: str
    prioridad: str  # alta | media | baja


@dataclass
class Empresa:
    id: int
    nombre: str
    sector: str
