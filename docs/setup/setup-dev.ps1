# Development setup script for Wealth Manager (PowerShell)
# This script helps developers set up the development environment securely

Write-Host "Setting up Wealth Manager development environment..." -ForegroundColor Blue

# Check if local.properties exists
if (-not (Test-Path "local.properties")) {
    Write-Host "Creating local.properties from template..." -ForegroundColor Yellow
    Copy-Item "local.properties.template" "local.properties"
    Write-Host "Please edit local.properties and add your API keys" -ForegroundColor Yellow
    Write-Host "See SECURITY.md for API key sources" -ForegroundColor Blue
} else {
    Write-Host "local.properties already exists" -ForegroundColor Green
}

# Check if API keys are set
$localProps = Get-Content "local.properties" -ErrorAction SilentlyContinue
if ($localProps -match "your_.*_api_key_here") {
    Write-Host "Please update API keys in local.properties" -ForegroundColor Yellow
    Write-Host "API Key Sources:" -ForegroundColor Blue
    Write-Host "   Finnhub: https://finnhub.io/register"
    Write-Host "   Exchange Rate API: https://exchangerate-api.com/"
} else {
    Write-Host "API keys appear to be configured" -ForegroundColor Green
}

# Check if .gitignore contains local.properties
$gitignore = Get-Content ".gitignore" -ErrorAction SilentlyContinue
if ($gitignore -match "local.properties") {
    Write-Host "local.properties is in .gitignore" -ForegroundColor Green
} else {
    Write-Host "local.properties is NOT in .gitignore" -ForegroundColor Red
    Write-Host "This is a security risk!" -ForegroundColor Yellow
}

# Check for hardcoded API keys in source code
Write-Host "Checking for hardcoded API keys in source code..." -ForegroundColor Blue
$hardcodedKeys = Get-ChildItem -Path "app\src" -Recurse -File -Include "*.kt", "*.java" | 
    Select-String -Pattern "api.*key.*=" -CaseSensitive:$false | 
    Where-Object { $_.Line -notmatch "BuildConfig" -and $_.Line -notmatch "your_.*_api_key_here" -and $_.Line -notmatch "buildConfigField" }

if ($hardcodedKeys) {
    Write-Host "Found potential hardcoded API keys in source code" -ForegroundColor Red
    Write-Host "Please remove hardcoded API keys and use BuildConfig instead" -ForegroundColor Yellow
    $hardcodedKeys | ForEach-Object { Write-Host "  $($_.Filename):$($_.LineNumber) - $($_.Line.Trim())" -ForegroundColor Red }
} else {
    Write-Host "No hardcoded API keys found in source code" -ForegroundColor Green
}

# Check if build.gradle has buildConfig enabled
$buildGradle = Get-Content "app\build.gradle" -ErrorAction SilentlyContinue
if ($buildGradle -match "buildConfig true") {
    Write-Host "BuildConfig is enabled in build.gradle" -ForegroundColor Green
} else {
    Write-Host "BuildConfig is not enabled in build.gradle" -ForegroundColor Yellow
    Write-Host "Add 'buildConfig true' to buildFeatures in build.gradle" -ForegroundColor Blue
}

# Check if API key fields are defined in build.gradle
if ($buildGradle -match "buildConfigField.*API_KEY") {
    Write-Host "API key fields are defined in build.gradle" -ForegroundColor Green
} else {
    Write-Host "API key fields are not defined in build.gradle" -ForegroundColor Yellow
    Write-Host "Add buildConfigField for API keys in build.gradle" -ForegroundColor Blue
}

# Security checklist
Write-Host ""
Write-Host "Security Checklist:" -ForegroundColor Blue
Write-Host "   local.properties in .gitignore"
Write-Host "   No hardcoded API keys in source code"
Write-Host "   BuildConfig enabled"
Write-Host "   API key fields defined"

# Development tips
Write-Host ""
Write-Host "Development Tips:" -ForegroundColor Blue
Write-Host "   Always use BuildConfig.FINNHUB_API_KEY in code"
Write-Host "   Never commit local.properties to version control"
Write-Host "   Rotate API keys regularly"
Write-Host "   Monitor API usage and set rate limits"
Write-Host "   Use different keys for development and production"

# Next steps
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Blue
Write-Host "   1. Edit local.properties with your actual API keys"
Write-Host "   2. Run '.\gradlew build' to test the build"
Write-Host "   3. Read docs/development/CONTRIBUTING.md for development guidelines"
Write-Host "   4. Read docs/security/SECURITY.md for security best practices"

Write-Host ""
Write-Host "Development environment setup complete!" -ForegroundColor Green
Write-Host "Read docs/development/CONTRIBUTING.md for development guidelines" -ForegroundColor Blue

Read-Host "Press Enter to continue"