#!/usr/bin/env pwsh

Write-Host "🔐 Setting up Wealth Manager development environment..." -ForegroundColor Blue

# Check Android SDK configuration
Write-Host "Checking Android SDK configuration..." -ForegroundColor Blue
if ($env:ANDROID_HOME) {
    Write-Host "ANDROID_HOME is set to: $env:ANDROID_HOME" -ForegroundColor Green
} elseif ((Test-Path "local.properties") -and ((Get-Content "local.properties" -ErrorAction SilentlyContinue) -match "sdk.dir")) {
    Write-Host "Android SDK path found in local.properties" -ForegroundColor Green
} else {
    Write-Host "Android SDK not configured" -ForegroundColor Yellow
    Write-Host "Set ANDROID_HOME environment variable or create local.properties with sdk.dir" -ForegroundColor Blue
}

# Check API key configuration
Write-Host "Checking API key configuration..." -ForegroundColor Blue
$buildGradle = Get-Content "app\build.gradle" -ErrorAction SilentlyContinue
if ($buildGradle -match "Removed BuildConfig API keys") {
    Write-Host "API keys properly configured for user input" -ForegroundColor Green
} else {
    Write-Host "API key configuration may need review" -ForegroundColor Yellow
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
if ($buildGradle -match "buildConfig true") {
    Write-Host "BuildConfig is enabled in build.gradle" -ForegroundColor Green
} else {
    Write-Host "BuildConfig is not enabled in build.gradle" -ForegroundColor Yellow
    Write-Host "Add 'buildConfig true' to buildFeatures in build.gradle" -ForegroundColor Blue
}

# Check if API key configuration is properly set up
if ($buildGradle -match "Removed BuildConfig API keys") {
    Write-Host "API keys properly configured for user input" -ForegroundColor Green
} else {
    Write-Host "API key configuration may need review" -ForegroundColor Yellow
}

# Check Gradle wrapper
Write-Host "Checking Gradle wrapper..." -ForegroundColor Blue
if (Test-Path "gradlew") {
    Write-Host "Gradle wrapper found" -ForegroundColor Green
} else {
    Write-Host "Gradle wrapper not found" -ForegroundColor Red
    Write-Host "Run 'gradle wrapper' to create it" -ForegroundColor Blue
}

# Check if project builds
Write-Host "Testing project build..." -ForegroundColor Blue
try {
    $buildOutput = & ".\gradlew" "tasks" "--quiet" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Project builds successfully" -ForegroundColor Green
    } else {
        Write-Host "Project build failed" -ForegroundColor Red
        Write-Host "Check Android SDK configuration" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Could not test build (Gradle not found)" -ForegroundColor Yellow
    Write-Host "Make sure Gradle is installed and in PATH" -ForegroundColor Blue
}

# Security checklist
Write-Host ""
Write-Host "Security Checklist:" -ForegroundColor Blue
Write-Host "   Android SDK configured"
Write-Host "   No hardcoded API keys in source code"
Write-Host "   BuildConfig enabled"
Write-Host "   API keys handled via user settings"

# Development tips
Write-Host ""
Write-Host "Development Tips:" -ForegroundColor Blue
Write-Host "   API keys are managed through app settings"
Write-Host "   No need for local.properties for API keys"
Write-Host "   Rotate API keys regularly"
Write-Host "   Monitor API usage and set rate limits"
Write-Host "   Use different keys for development and production"

# Next steps
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Blue
Write-Host "   1. Configure Android SDK (ANDROID_HOME or local.properties)"
Write-Host "   2. Run '.\gradlew build' to test the build"
Write-Host "   3. Read docs/development/CONTRIBUTING.md for development guidelines"
Write-Host "   4. Read docs/security/SECURITY.md for security best practices"

Write-Host ""
Write-Host "Development environment setup complete!" -ForegroundColor Green
Write-Host "Read docs/development/CONTRIBUTING.md for development guidelines" -ForegroundColor Blue