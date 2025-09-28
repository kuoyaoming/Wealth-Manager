@echo off
chcp 65001 >nul
echo WealthManager APK Installer and Logger Script (Fixed Version)
echo ================================================

REM Check if PowerShell is available
powershell -Command "Get-Host" >nul 2>&1
if errorlevel 1 (
    echo PowerShell not available
    echo Please ensure PowerShell 5.0 or higher is installed
    pause
    exit /b 1
)

REM Check if APK file exists
if not exist "WealthManager_v0.1.6_final.apk" (
    echo APK file not found: WealthManager-v0.1.5-complete.apk
    echo Please ensure the APK file is in the current directory
    pause
    exit /b 1
)

echo APK file found: WealthManager-v0.1.5-complete.apk
echo.

REM Execute PowerShell script
powershell -ExecutionPolicy Bypass -File wealthmanager_logger_fixed.ps1

echo.
echo Press any key to exit...
pause >nul
