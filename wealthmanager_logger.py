#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
WealthManager APK 安裝和日誌記錄腳本
專門用於 WealthManager 應用程式的自動化安裝和日誌記錄
"""

import subprocess
import sys
import os
import time
import signal
import threading
from datetime import datetime


class WealthManagerLogger:
    def __init__(self):
        self.log_process = None
        self.log_file = None
        self.package_name = "com.wealthmanager"
        self.is_running = True
        
    def run_command(self, command, check=True):
        """執行命令並返回結果"""
        try:
            result = subprocess.run(command, shell=True, capture_output=True, text=True, check=check)
            return result.stdout.strip(), result.stderr.strip()
        except subprocess.CalledProcessError as e:
            print(f"命令執行失敗: {command}")
            print(f"錯誤: {e.stderr}")
            return None, e.stderr
    
    def check_prerequisites(self):
        """檢查前置條件"""
        print("🔍 檢查前置條件...")
        
        # 檢查 ADB
        stdout, stderr = self.run_command("adb version", check=False)
        if stderr or "Android Debug Bridge" not in stdout:
            print("❌ ADB 工具不可用")
            return False
        print("✅ ADB 工具可用")
        
        # 檢查設備
        stdout, stderr = self.run_command("adb devices")
        if not stdout or "device" not in stdout:
            print("❌ 沒有檢測到連接的 Android 設備")
            return False
        print("✅ 設備已連接")
        
        return True
    
    def uninstall_existing(self):
        """卸載已存在的 WealthManager"""
        print(f"🔍 檢查是否已安裝 {self.package_name}...")
        stdout, stderr = self.run_command(f"adb shell pm list packages | findstr {self.package_name}", check=False)
        
        if self.package_name in stdout:
            print(f"🗑️ 卸載已存在的應用程式...")
            stdout, stderr = self.run_command(f"adb uninstall {self.package_name}", check=False)
            if "Success" in stdout:
                print("✅ 應用程式已成功卸載")
            else:
                print(f"⚠️ 卸載可能失敗: {stderr}")
        else:
            print("✅ 沒有發現已安裝的 WealthManager")
    
    def install_apk(self, apk_path):
        """安裝 APK"""
        if not os.path.exists(apk_path):
            print(f"❌ APK 檔案不存在: {apk_path}")
            return False
        
        print(f"📱 安裝 APK: {apk_path}")
        stdout, stderr = self.run_command(f'adb install -r "{apk_path}"', check=False)
        
        if "Success" in stdout:
            print("✅ APK 安裝成功")
            return True
        else:
            print(f"❌ APK 安裝失敗: {stderr}")
            return False
    
    def start_app(self):
        """啟動 WealthManager 應用程式"""
        print(f"🚀 啟動 WealthManager...")
        stdout, stderr = self.run_command(f"adb shell am start -n {self.package_name}/.MainActivity", check=False)
        
        if "Starting:" in stdout or "Warning:" in stdout:
            print("✅ 應用程式已啟動")
            return True
        else:
            print(f"⚠️ 應用程式啟動可能失敗: {stderr}")
            return False
    
    def start_logging(self):
        """開始記錄日誌"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.log_file = f"WealthManager_logs_{timestamp}.txt"
        
        print(f"📝 開始記錄日誌到: {self.log_file}")
        print("📊 僅記錄 APP 相關日誌 (VERBOSE, DEBUG, INFO, WARN, ERROR)")
        
        # 清除現有日誌
        self.run_command("adb logcat -c", check=False)
        
        # 嘗試以 pid 過濾 APP 日誌
        pid_out, _ = self.run_command(f"adb shell pidof {self.package_name}", check=False)
        if pid_out and pid_out.strip():
            pid_csv = ",".join(pid_out.split())
            log_command = f'adb logcat -v time --pid {pid_csv} > "{self.log_file}"'
        else:
            # 後備方案：以應用自有 tag 過濾
            log_command = f'adb logcat -v time "WealthManagerDebug:*" "*:S" > "{self.log_file}"'

        self.log_process = subprocess.Popen(log_command, shell=True)
        
        print("✅ 日誌記錄已開始")
        return True
    
    def monitor_app(self):
        """監控應用程式狀態"""
        print("👀 監控 WealthManager 狀態...")
        print("📝 日誌記錄將持續到您按 Ctrl+C 為止")
        print("📱 應用程式可以關閉並重新開啟，日誌記錄會持續進行")
        print("💡 按 Ctrl+C 停止記錄")
        print("=" * 50)
        
        try:
            while self.is_running:
                # 使用 pidof 檢查應用程式是否在運行（較準確）
                stdout, stderr = self.run_command(f"adb shell pidof {self.package_name}", check=False)
                if stdout and stdout.strip():
                    print(f"✅ WealthManager 正在運行 (pid: {stdout.strip()}) - 記錄中...")
                else:
                    print(f"📱 WealthManager 未運行（您可以重新啟動它）")
                
                time.sleep(5)  # 每5秒檢查一次
                
        except KeyboardInterrupt:
            print("\n🛑 收到中斷信號")
            self.is_running = False
    
    def stop_logging(self):
        """停止日誌記錄"""
        if self.log_process:
            print("🛑 停止日誌記錄...")
            self.log_process.terminate()
            self.log_process.wait()
            print("✅ 日誌記錄已停止")
    
    def run(self, apk_path):
        """主要執行流程"""
        print("🚀 WealthManager APK 安裝和日誌記錄腳本")
        print("=" * 50)
        
        try:
            # 1. 檢查前置條件
            if not self.check_prerequisites():
                return False
            
            # 2. 卸載已存在的應用程式
            self.uninstall_existing()
            
            # 3. 安裝 APK
            if not self.install_apk(apk_path):
                return False
            
            # 4. 啟動應用程式
            if not self.start_app():
                return False
            
            # 5. 開始記錄日誌
            if not self.start_logging():
                return False
            
            # 6. 監控應用程式狀態
            self.monitor_app()
            
            # 7. 停止日誌記錄
            self.stop_logging()
            
            print(f"\n✅ 日誌已保存到: {self.log_file}")
            print("🎉 任務完成！")
            
        except Exception as e:
            print(f"❌ 發生錯誤: {e}")
            self.stop_logging()
            return False
        
        return True


def main():
    if len(sys.argv) != 2:
        print("使用方法: python wealthmanager_logger.py <APK檔案路徑>")
        print("範例: python wealthmanager_logger.py WealthManager-v0.1.5-complete.apk")
        sys.exit(1)
    
    apk_path = sys.argv[1]
    
    logger = WealthManagerLogger()
    success = logger.run(apk_path)
    
    if not success:
        sys.exit(1)


if __name__ == "__main__":
    main()
