@echo off
rem TourInvest - lanzador universal del setup (funciona tambien con doble clic)
rem Uso: setup.cmd [check|seed|run-backend|run-frontend|help]
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" %*
if "%~1"=="" pause