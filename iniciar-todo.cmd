@echo off
rem ============================================================
rem  TourInvest - ARRANQUE COMPLETO (2 ventanas visibles)
rem  1) Ventana BACKEND  : API Spring Boot en http://localhost:8080
rem  2) Ventana FRONTEND : sitio estatico en http://localhost:8081
rem     (el frontend abre tu navegador automaticamente)
rem  Para DETENER: cierra cada ventana (X) o pulsa Ctrl+C dentro.
rem ============================================================
title TourInvest - lanzador completo

echo Abriendo ventana del BACKEND (:8080)...
start "TourInvest BACKEND :8080 (NO CERRAR mientras uses la app)" powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" run-backend

echo Esperando 8 s para que el backend levante...
timeout /t 8 /nobreak >nul

echo Abriendo ventana del FRONTEND (:8081 + navegador)...
start "TourInvest FRONTEND :8081 (NO CERRAR mientras uses la app)" powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" run-frontend

echo.
echo   Listo. Usa la pagina que se abrio:  http://localhost:8081/login.html
echo   Usuarios: nuevo@tourinvest.com (Admin) / laura@ (Analista) / carlos@ (Inversionista)
echo   Clave para todos: 123456
echo.
echo   Esta ventana puedes cerrarla; las dos ventanas de servidores deben quedar abiertas.
pause