@echo off
title Spring Boot + ngrok auto start

:: 1. 백엔드 실행 (cmd 새창으로)
start cmd /k "cd /d D:\R-maGO\Back && ./gradlew bootRun"

:: 2. ngrok 실행 (10초 뒤 실행)
timeout /t 10 >nul
start cmd /k "cd /d C:\Users\arto1\OneDrive\Desktop\private\download\ngrok-v3-stable-windows-amd64 && ngrok.exe http --domain=loving-snipe-sharp.ngrok-free.app 8080"
