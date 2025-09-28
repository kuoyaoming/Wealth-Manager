# WealthManager APK Installer and Logger Script
# Auto install APK, start app, record complete logs

param(
    [string]$ApkPath = "WealthManager-v0.1.5-complete.apk"
)

$PackageName = "com.wealthmanager"
$LogProcess = $null
$IsRunning = $true

function Write-Status {
    param(
        [string]$Message,
        [string]$Type = "Info"
    )
    
    $color = switch ($Type) {
        "Success" { "Green" }
        "Error" { "Red" }
        "Warning" { "Yellow" }
        "Info" { "Cyan" }
        default { "White" }
    }
    
    Write-Host $Message -ForegroundColor $color
}

function Test-ADB {
    try {
        $result = & adb version 2>&1
        if ($result -match "Android Debug Bridge") {
            return $true
        }
        return $false
    }
    catch {
        return $false
    }
}

function Test-Device {
    try {
        $result = & adb devices 2>&1
        if ($result -match "device") {
            return $true
        }
        return $false
    }
    catch {
        return $false
    }
}

function Invoke-ADB {
    param([string[]]$ArgsArray)
    try {
        $result = & adb @ArgsArray 2>&1
        return $result
    }
    catch {
        Write-Status ("ADB command failed: " + ($ArgsArray -join ' ')) "Error"
        return $null
    }
}

function Test-Prerequisites {
    Write-Status "Checking prerequisites..." "Info"
    
    # Check ADB
    if (-not (Test-ADB)) {
        Write-Status "ADB tool not available" "Error"
        return $false
    }
    Write-Status "ADB tool available" "Success"
    
    # Check device
    if (-not (Test-Device)) {
        Write-Status "No Android device detected" "Error"
        return $false
    }
    Write-Status "Device connected" "Success"
    
    # Check APK file
    if (-not (Test-Path $ApkPath)) {
        Write-Status "APK file not found: $ApkPath" "Error"
        return $false
    }
    Write-Status "APK file found: $ApkPath" "Success"
    
    return $true
}

function Remove-ExistingApp {
    Write-Status "Checking for existing app..." "Info"
    
    # Robust check on device side: use pm path to see if package is installed
    $packages = Invoke-ADB @("shell", "pm", "path", $PackageName)
    if ($packages -match "package:") {
        Write-Status "Uninstalling existing app..." "Warning"
        $result = Invoke-ADB "uninstall $PackageName"
        if ($result -match "Success") {
            Write-Status "App uninstalled successfully" "Success"
        } else {
            Write-Status "Uninstall may have failed" "Warning"
        }
    } else {
        Write-Status "No existing app found" "Success"
    }
}

function Install-APK {
    Write-Status "Installing APK: $ApkPath" "Info"
    
    $result = Invoke-ADB @("install", "-r", $ApkPath)
    if ($result -match "Success") {
        Write-Status "APK installed successfully" "Success"
        return $true
    } else {
        Write-Status "APK installation failed: $result" "Error"
        return $false
    }
}

function Start-App {
    Write-Status "Starting WealthManager..." "Info"
    
    $result = Invoke-ADB @("shell", "am", "start", "-n", "$PackageName/.MainActivity")
    if ($result -match "Starting:" -or $result -match "Warning:") {
        Write-Status "App started successfully" "Success"
        return $true
    } else {
        Write-Status "App start may have failed: $result" "Warning"
        return $false
    }
}

function Start-Logging {
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $LogFile = "WealthManager_logs_$timestamp.txt"
    
    Write-Status "Starting log recording to: $LogFile" "Info"
    Write-Status "Recording app-only logs (VERBOSE, DEBUG, INFO, WARN, ERROR)" "Info"
    
    # Clear existing logs
    Invoke-ADB @("logcat", "-c") | Out-Null
    
    # Resolve app PID(s) with retry (in case process starts slightly later)
    $pidCsv = $null
    for ($i = 0; $i -lt 10; $i++) {
        $pidRaw = Invoke-ADB @("shell", "pidof", $PackageName)
        if ($pidRaw -and $pidRaw.Trim().Length -gt 0) {
            # pidof may return space-separated list; convert to comma-separated for logcat
            $pidCsv = ($pidRaw -replace "\s+", ",").Trim(',')
            break
        }
        Start-Sleep -Milliseconds 500
    }

    if ($pidCsv) {
        Write-Status ("Using PID(s) for filtering: " + $pidCsv) "Info"
        $script:LogProcess = Start-Process -FilePath "adb" -ArgumentList "logcat", "-v", "time", "--pid", $pidCsv -RedirectStandardOutput $LogFile -PassThru -WindowStyle Hidden
    } else {
        # Fallback: narrow to likely app tags only to avoid full device spam
        Write-Status "PID not found, falling back to tag filter (WealthManagerDebug)" "Warning"
        $script:LogProcess = Start-Process -FilePath "adb" -ArgumentList "logcat", "-v", "time", "WealthManagerDebug:*", "*:S" -RedirectStandardOutput $LogFile -PassThru -WindowStyle Hidden
    }
    
    Write-Status "Log recording started" "Success"
    return $LogFile
}

function Monitor-App {
    Write-Status "Monitoring WealthManager status..." "Info"
    Write-Status "Log recording will continue until you press Ctrl+C" "Info"
    Write-Status "The app can be closed and reopened, logging will continue" "Info"
    Write-Status "Press Ctrl+C to stop recording" "Warning"
    Write-Status "=" * 50 "Info"
    
    while ($IsRunning) {
        try {
            # Use pidof to check if the process is running
            $pid = Invoke-ADB @("shell", "pidof", $PackageName)
            if ($pid -and ($pid.Trim().Length -gt 0)) {
                Write-Status "WealthManager is running (pid: $pid) - logging..." "Success"
            } else {
                Write-Status "WealthManager is not running (you can restart it)" "Info"
            }
        }
        catch {
            Write-Status "Transient monitoring error, continuing..." "Warning"
        }
        Start-Sleep -Seconds 5
    }
}

function Stop-Logging {
    if ($LogProcess -and !$LogProcess.HasExited) {
        Write-Status "Stopping log recording..." "Warning"
        $LogProcess.Kill()
        $LogProcess.WaitForExit()
        Write-Status "Log recording stopped" "Success"
    }
}

function Main {
    Write-Status "WealthManager APK Installer and Logger Script" "Info"
    Write-Status "=" * 50 "Info"
    
    try {
        # 1. Check prerequisites
        if (-not (Test-Prerequisites)) {
            return $false
        }
        
        # 2. Remove existing app
        Remove-ExistingApp
        
        # 3. Install APK
        if (-not (Install-APK)) {
            return $false
        }
        
        # 4. Start app
        if (-not (Start-App)) {
            return $false
        }
        
        # 5. Start logging
        $LogFile = Start-Logging
        
        # 6. Monitor app status
        Monitor-App
        
        # 7. Stop logging
        Stop-Logging
        
        Write-Status "Log saved to: $LogFile" "Success"
        Write-Status "Task completed!" "Success"
        
    }
    catch {
        Write-Status "Error occurred: $($_.Exception.Message)" "Error"
        Stop-Logging
        return $false
    }
    
    return $true
}

# Set up interrupt handling
$null = Register-EngineEvent -SourceIdentifier PowerShell.Exiting -Action {
    $script:IsRunning = $false
    Stop-Logging
}

# Execute main flow
$success = Main

if (-not $success) {
    exit 1
}
