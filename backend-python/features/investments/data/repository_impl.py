from core.database import PORTAFOLIO_DB, ALERTAS_DB, EMPRESAS_DB


class InvestmentsRepositoryImpl:

    def obtener_portafolio(self):
        """RF07 - Mostrar portafolio"""
        return PORTAFOLIO_DB

    def obtener_alertas(self):
        return ALERTAS_DB

    def obtener_empresas(self):
        return EMPRESAS_DB

    def valor_total_portafolio(self):
        # Cálculo simple de ejemplo a partir de la participación ponderada
        return sum(p["participacion"] * 1_000_000 for p in PORTAFOLIO_DB)
