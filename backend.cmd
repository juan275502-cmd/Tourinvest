@echo off
title TourInvest - Backend Spring Boot (:8080)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" run-backend
pause