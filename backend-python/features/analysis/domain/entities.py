from dataclasses import dataclass


@dataclass
class Indicador:
    indicador: str
    empresa: str
    valor: float


@dataclass
class Reporte:
    titulo: str
    empresa: str
    fecha: str
