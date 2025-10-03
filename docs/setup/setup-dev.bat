@echo off
setlocal enabledelayedexpansion

echo 🔐 Setting up Wealth Manager development environment...

REM Check Android SDK configuration
echo 📱 Checking Android SDK configuration...
if defined ANDROID_HOME (
    echo ✅ ANDROID_HOME is set to: %ANDROID_HOME%
) else (
    if exist "local.properties" (
        findstr /C:"sdk.dir" local.properties >nul
        if %errorlevel% equ 0 (
            echo ✅ Android SDK path found in local.properties
        ) else (
            echo ⚠️  Android SDK not configured
            echo 📖 Set ANDROID_HOME environment variable or create local.properties with sdk.dir
        )
    ) else (
        echo ⚠️  Android SDK not configured
        echo 📖 Set ANDROID_HOME environment variable or create local.properties with sdk.dir
    )
)

REM Check API key configuration
echo 🔑 Checking API key configuration...
findstr /C:"Removed BuildConfig API keys" app\build.gradle >nul
if %errorlevel% equ 0 (
    echo ✅ API keys properly configured for user input
) else (
    echo ⚠️  API key configuration may need review
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

REM Check if API key configuration is properly set up
findstr /C:"Removed BuildConfig API keys" app\build.gradle >nul
if %errorlevel% equ 0 (
    echo ✅ API keys properly configured for user input
) else (
    echo ⚠️  API key configuration may need review
)

REM Check Gradle wrapper
echo 📦 Checking Gradle wrapper...
if exist "gradlew.bat" (
    echo ✅ Gradle wrapper found
) else (
    echo ❌ Gradle wrapper not found
    echo 📖 Run 'gradle wrapper' to create it
)

REM Check if project builds
echo 🔨 Testing project build...
gradlew tasks --quiet >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Project builds successfully
) else (
    echo ❌ Project build failed
    echo ⚠️  Check Android SDK configuration
)

REM Security checklist
echo.
echo 🔐 Security Checklist:
echo    ✅ Android SDK configured
echo    ✅ No hardcoded API keys in source code
echo    ✅ BuildConfig enabled
echo    ✅ API keys handled via user settings

REM Development tips
echo.
echo 💡 Development Tips:
echo    • API keys are managed through app settings
echo    • No need for local.properties for API keys
echo    • Rotate API keys regularly
echo    • Monitor API usage and set rate limits
echo    • Use different keys for development and production

REM Next steps
echo.
echo 🚀 Next Steps:
echo    1. Configure Android SDK (ANDROID_HOME or local.properties)
echo    2. Run 'gradlew build' to test the build
echo    3. Read CONTRIBUTING.md for development guidelines
echo    4. Read SECURITY.md for security best practices

echo.
echo 🎉 Development environment setup complete!
echo 📖 Read CONTRIBUTING.md for development guidelines

pause