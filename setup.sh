#!/usr/bin/env bash
# TourInvest — setup.sh
# Helper de montaje: check de requisitos + seed de BD + pistas de arranque.
# NOTA: este script solo sirve en Linux/macOS (necesita bash).
#       En WINDOWS usa su equivalente nativo: .\setup.ps1
# Uso:
#   ./setup.sh check        # verifica JDK, Maven, MySQL/MariaDB, Node, Python 
#   ./setup.sh seed         # carga query.sql (raiz del proyecto) en la BD 'tourinvest'
#   ./setup.sh run-backend  # arranca Spring Boot (:8080)
#   ./setup.sh run-frontend # arranca servidor estático (:8081)
#   ./setup.sh help
# deactivate -nondestructive   


set -u
ROOT="$(cd "$(dirname "$0")" && pwd)"
# Fallback: algunos sistemas (Windows incluido) no tienen 'python3'
PY="python3"
command -v python3 >/dev/null 2>&1 || PY="python"

check() {
  local ok=1
  echo "== TourInvest — check de requisitos =="
  for tool in java mvn mysql node python3; do
    if command -v "$tool" >/dev/null 2>&1; then
      echo "  [OK] $tool  ->  $(command -v "$tool")"
    else
      echo "  [FALTA] $tool"
      ok=0
    fi
  done
  if command -v java >/dev/null 2>&1; then
    java -version 2>&1 | head -n1 | sed 's/^/  Java: /'
  fi
  exit $((ok ? 0 : 1))
}

seed() {
  echo "== Cargando esquema + datos de prueba (query.sql en la RAIZ del proyecto) =="
  if ! command -v mysql >/dev/null 2>&1; then
    echo "mysql no esta disponible. No se puede sembrar la BD."; exit 1
  fi
  if [ ! -f "$ROOT/query.sql" ]; then
    echo "No se encontro $ROOT/query.sql"; exit 1
  fi
  # Pide password de root y ejecuta query.sql (crea 'tourinvest' y seed).
  mysql --default-character-set=utf8mb4 -u root -p tourinvest < "$ROOT/query.sql"
  echo "  OK. Usuarios semilla (clave 123456):"
  echo "    nuevo@tourinvest.com   (Administrador; query.sql renombra el original juan@)"
  echo "    laura@tourinvest.com   (Analista)"
  echo "    carlos@tourinvest.com  (Inversionista)"
}

run_backend() {
  cd "$ROOT/backend-java" || exit 1
  mvn spring-boot:run
}

run_frontend() {
  cd "$ROOT/frontend" || exit 1
  echo "Frontend en http://localhost:8081"
  "$PY" -m http.server 8081
}

helpMsg() {
  sed -n '1,40p' "$ROOT/setup.sh" 2>/dev/null || echo "Uso: check | seed | run-backend | run-frontend | help"
}

case "$1" in
  check) check ;;
  seed)  seed ;;
  run-backend)  run_backend ;;
  run-frontend) run_frontend ;;
  help|"") helpMsg ;;
  *) echo "Accion desconocida: $1"; helpMsg; exit 2 ;;
esac