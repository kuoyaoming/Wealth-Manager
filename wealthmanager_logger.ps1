# WealthManager APK Installer and Logger Script (PowerShell Version)
# Function: Auto install APK, start app, record complete logs

param(
    [string]$ApkPath = "WealthManager-v0.1.5-complete.apk"
)

$script:PackageName = "com.wealthmanager"
$script:LogProcess = $null
$script:IsRunning = $true

function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Test-Command {
    param([string]$Command)
    try {
        $null = Get-Command $Command -ErrorAction Stop
        return $true
    }
    catch {
        return $false
    }
}

function Invoke-ADBCommand {
    param([string]$Command)
    try {
        $result = Invoke-Expression "adb $Command" 2>&1
        return $result
    }
    catch {
        Write-ColorOutput "❌ ADB 命令執行失敗: $Command" "Red"
        return $null
    }
}

function Test-Prerequisites {
    Write-ColorOutput "Checking prerequisites..." "Yellow"
    
    # Check ADB
    if (-not (Test-Command "adb")) {
        Write-ColorOutput "ADB tool not available, please install Android SDK" "Red"
        return $false
    }
    Write-ColorOutput "ADB tool available" "Green"
    
    # Check device
    $devices = Invoke-ADBCommand "devices"
    if (-not $devices -or $devices -notmatch "device") {
        Write-ColorOutput "No Android device detected" "Red"
        Write-ColorOutput "Please ensure device is connected and USB debugging is enabled" "Yellow"
        return $false
    }
    Write-ColorOutput "Device connected" "Green"
    
    # Check APK file
    if (-not (Test-Path $ApkPath)) {
        Write-ColorOutput "APK file not found: $ApkPath" "Red"
        return $false
    }
    Write-ColorOutput "APK file found: $ApkPath" "Green"
    
    return $true
}

function Remove-ExistingApp {
    Write-ColorOutput "🔍 檢查是否已安裝 $PackageName..." "Yellow"
    
    $packages = Invoke-ADBCommand "shell pm list packages | findstr $PackageName"
    if ($packages -match $PackageName) {
        Write-ColorOutput "🗑️ 卸載已存在的應用程式..." "Yellow"
        $result = Invoke-ADBCommand "uninstall $PackageName"
        if ($result -match "Success") {
            Write-ColorOutput "✅ 應用程式已成功卸載" "Green"
        } else {
            Write-ColorOutput "⚠️ 卸載可能失敗" "Yellow"
        }
    } else {
        Write-ColorOutput "✅ 沒有發現已安裝的 WealthManager" "Green"
    }
}

function Install-APK {
    Write-ColorOutput "📱 安裝 APK: $ApkPath" "Yellow"
    
    $result = Invoke-ADBCommand "install -r `"$ApkPath`""
    if ($result -match "Success") {
        Write-ColorOutput "✅ APK 安裝成功" "Green"
        return $true
    } else {
        Write-ColorOutput "❌ APK 安裝失敗: $result" "Red"
        return $false
    }
}

function Start-App {
    Write-ColorOutput "🚀 啟動 WealthManager..." "Yellow"
    
    $result = Invoke-ADBCommand "shell am start -n $PackageName/.MainActivity"
    if ($result -match "Starting:" -or $result -match "Warning:") {
        Write-ColorOutput "✅ 應用程式已啟動" "Green"
        return $true
    } else {
        Write-ColorOutput "⚠️ 應用程式啟動可能失敗: $result" "Yellow"
        return $false
    }
}

function Start-Logging {
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $LogFile = "WealthManager_logs_$timestamp.txt"
    
    Write-ColorOutput "📝 開始記錄日誌到: $LogFile" "Yellow"
    Write-ColorOutput "📊 記錄所有層級的日誌 (VERBOSE, DEBUG, INFO, WARN, ERROR)" "Yellow"
    
    # 清除現有日誌
    Invoke-ADBCommand "logcat -c" | Out-Null
    
    # 開始記錄日誌
    $LogProcess = Start-Process -FilePath "adb" -ArgumentList "logcat", "-v", "time", "*:V" -RedirectStandardOutput $LogFile -PassThru -WindowStyle Hidden
    
    Write-ColorOutput "✅ 日誌記錄已開始" "Green"
    return $LogFile, $LogProcess
}

function Monitor-App {
    Write-ColorOutput "👀 監控 WealthManager 狀態..." "Yellow"
    Write-ColorOutput "💡 按 Ctrl+C 停止記錄" "Cyan"
    Write-ColorOutput "=" * 50 "Cyan"
    
    try {
        while ($IsRunning) {
            $processes = Invoke-ADBCommand "shell ps | findstr $PackageName"
            
            if ($processes -notmatch $PackageName) {
                Write-ColorOutput "📱 WealthManager 已關閉" "Yellow"
                break
            }
            
            Start-Sleep -Seconds 3
        }
    }
    catch {
        Write-ColorOutput "🛑 收到中斷信號" "Yellow"
        $IsRunning = $false
    }
}

function Stop-Logging {
    if ($LogProcess -and !$LogProcess.HasExited) {
        Write-ColorOutput "🛑 停止日誌記錄..." "Yellow"
        $LogProcess.Kill()
        $LogProcess.WaitForExit()
        Write-ColorOutput "✅ 日誌記錄已停止" "Green"
    }
}

# 主執行流程
function Main {
    Write-ColorOutput "🚀 WealthManager APK 安裝和日誌記錄腳本" "Cyan"
    Write-ColorOutput "=" * 50 "Cyan"
    
    try {
        # 1. 檢查前置條件
        if (-not (Test-Prerequisites)) {
            return $false
        }
        
        # 2. 卸載已存在的應用程式
        Remove-ExistingApp
        
        # 3. 安裝 APK
        if (-not (Install-APK)) {
            return $false
        }
        
        # 4. 啟動應用程式
        if (-not (Start-App)) {
            return $false
        }
        
        # 5. 開始記錄日誌
        $LogFile, $LogProcess = Start-Logging
        
        # 6. 監控應用程式狀態
        Monitor-App
        
        # 7. 停止日誌記錄
        Stop-Logging
        
        Write-ColorOutput "`n✅ 日誌已保存到: $LogFile" "Green"
        Write-ColorOutput "🎉 任務完成！" "Green"
        
    }
    catch {
        Write-ColorOutput "❌ 發生錯誤: $($_.Exception.Message)" "Red"
        Stop-Logging
        return $false
    }
    
    return $true
}

# 設定中斷處理
$null = Register-EngineEvent -SourceIdentifier PowerShell.Exiting -Action {
    $IsRunning = $false
    Stop-Logging
}

# 執行主流程
$success = Main

if (-not $success) {
    exit 1
}
