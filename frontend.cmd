@echo off
title TourInvest - Frontend (:8081, abre el navegador)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" run-frontend
pause