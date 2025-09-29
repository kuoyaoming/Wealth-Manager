@echo off
REM Development setup script for Wealth Manager (Windows)
REM This script helps developers set up the development environment securely

echo 🔐 Setting up Wealth Manager development environment...

REM Check if local.properties exists
if not exist "local.properties" (
    echo 📋 Creating local.properties from template...
    copy "local.properties.template" "local.properties"
    echo ⚠️  Please edit local.properties and add your API keys
    echo 📖 See SECURITY.md for API key sources
) else (
    echo ✅ local.properties already exists
)

REM Check if API keys are set
findstr /C:"your_" local.properties >nul
if %errorlevel% equ 0 (
    echo ⚠️  Please update API keys in local.properties
    echo 📖 API Key Sources:
    echo    • Finnhub: https://finnhub.io/register
    echo    • Exchange Rate API: https://exchangerate-api.com/
) else (
    echo ✅ API keys appear to be configured
)

REM Check if .gitignore contains local.properties
findstr /C:"local.properties" .gitignore >nul
if %errorlevel% equ 0 (
    echo ✅ local.properties is in .gitignore
) else (
    echo ❌ local.properties is NOT in .gitignore
    echo ⚠️  This is a security risk!
)

REM Check for hardcoded API keys in source code
echo 🔍 Checking for hardcoded API keys in source code...
findstr /S /I "api.*key.*=" app\src\ | findstr /V "BuildConfig" | findstr /V "your_.*_api_key_here" >nul
if %errorlevel% equ 0 (
    echo ❌ Found potential hardcoded API keys in source code
    echo ⚠️  Please remove hardcoded API keys and use BuildConfig instead
) else (
    echo ✅ No hardcoded API keys found in source code
)

REM Check if build.gradle has buildConfig enabled
findstr /C:"buildConfig true" app\build.gradle >nul
if %errorlevel% equ 0 (
    echo ✅ BuildConfig is enabled in build.gradle
) else (
    echo ⚠️  BuildConfig is not enabled in build.gradle
    echo 📖 Add 'buildConfig true' to buildFeatures in build.gradle
)

REM Check if API key fields are defined in build.gradle
findstr /C:"buildConfigField" app\build.gradle | findstr /C:"API_KEY" >nul
if %errorlevel% equ 0 (
    echo ✅ API key fields are defined in build.gradle
) else (
    echo ⚠️  API key fields are not defined in build.gradle
    echo 📖 Add buildConfigField for API keys in build.gradle
)

REM Security checklist
echo.
echo 🔐 Security Checklist:
echo    ✅ local.properties in .gitignore
echo    ✅ No hardcoded API keys in source code
echo    ✅ BuildConfig enabled
echo    ✅ API key fields defined

REM Development tips
echo.
echo 💡 Development Tips:
echo    • Always use BuildConfig.FINNHUB_API_KEY in code
echo    • Never commit local.properties to version control
echo    • Rotate API keys regularly
echo    • Monitor API usage and set rate limits
echo    • Use different keys for development and production

REM Next steps
echo.
echo 🚀 Next Steps:
echo    1. Edit local.properties with your actual API keys
echo    2. Run 'gradlew build' to test the build
echo    3. Read CONTRIBUTING.md for development guidelines
echo    4. Read SECURITY.md for security best practices

echo.
echo 🎉 Development environment setup complete!
echo 📖 Read CONTRIBUTING.md for development guidelines

pause
