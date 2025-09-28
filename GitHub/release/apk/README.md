# Wealth Manager APK Downloads

## 📱 Available APK Versions

### 🚀 Latest Release: v0.1.1

| APK File | Size | Description |
|----------|------|-------------|
| `WealthManager-v0.1.1-final.apk` | 12K | **Complete APK** with all project resources and structure |
| `WealthManager-v0.1.1-complete.apk` | 8K | Complete APK structure with basic resources |
| `WealthManager-v0.1.1-minimal.apk` | 4K | Minimal APK for testing purposes |

## 🎯 Recommended Download

**Download: `WealthManager-v0.1.1-final.apk`** - This is the most complete version with all project resources.

## 📋 APK Contents

### ✅ Included Features
- **Complete AndroidManifest.xml** with all permissions and activities
- **Full resource files** (strings, colors, themes) in English and Chinese
- **App icons** and visual assets
- **Security configurations** (backup rules, data extraction rules)
- **Multi-language support** (English/Chinese)
- **Material You theming** support

### ⚠️ Limitations
- **No compiled code** (classes.dex is placeholder)
- **No native libraries** (lib/ directories are empty)
- **No signing certificate** (CERT.RSA is placeholder)
- **Not installable** without proper Android SDK compilation

## 🔧 How to Use These APKs

### For Development
1. **Use as reference** for project structure
2. **Extract resources** for your own builds
3. **Study AndroidManifest.xml** for configuration
4. **Copy resource files** to your Android Studio project

### For Testing
1. **Use Android Studio** to build a proper APK
2. **Follow BUILD_INSTRUCTIONS.md** for complete build process
3. **Install on physical device** for full functionality testing

## 🛠 Building a Functional APK

### Prerequisites
- Android Studio (latest version)
- Android SDK (API 23+)
- Java 8+ or Kotlin support

### Quick Build Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/kuoyaoming/Wealth-Manager.git
   cd Wealth-Manager
   ```

2. **Open in Android Studio**
   - File → Open → Select Wealth-Manager folder
   - Wait for Gradle sync to complete

3. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or use terminal: `./gradlew assembleDebug`

4. **Install APK**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## 📱 System Requirements

### Minimum Requirements
- **Android Version**: 6.0 (API 23) or higher
- **RAM**: 2GB minimum
- **Storage**: 100MB available space
- **Hardware**: Biometric sensor (fingerprint/face recognition)

### Recommended Requirements
- **Android Version**: 12.0 (API 31) or higher for Material You
- **RAM**: 4GB or more
- **Storage**: 500MB available space
- **Network**: Internet connection for market data

## 🔒 Security Features

### Data Protection
- **Local storage only** - No cloud synchronization
- **Biometric authentication** - Secure app access
- **Encrypted database** - Room database with encryption
- **No network permissions** except for market data

### Privacy Compliance
- **No data collection** - All data stays on device
- **No analytics** - No user tracking
- **No advertisements** - Clean, ad-free experience
- **Open source** - Transparent codebase

## 🌐 Multi-language Support

### Supported Languages
- **English (en-US)** - Default language
- **Traditional Chinese (zh-TW)** - Auto-detected

### Language Detection
- Automatically detects system language
- Falls back to English for unsupported languages
- All UI elements properly localized

## 📊 Features Overview

### Core Functionality
- ✅ **Biometric Authentication** - Secure app access
- ✅ **Asset Management** - Cash and stock tracking
- ✅ **Portfolio Dashboard** - Total asset overview
- ✅ **Multi-currency Support** - TWD/USD conversion
- ✅ **Local Database** - Room database storage
- ✅ **Material You Theming** - Dynamic color adaptation

### UI/UX Features
- ✅ **Modern Design** - Material 3 components
- ✅ **Responsive Layout** - Adapts to screen sizes
- ✅ **Dark/Light Theme** - System theme following
- ✅ **Smooth Animations** - Jetpack Compose animations
- ✅ **Accessibility** - Screen reader support

## 🐛 Known Issues

### Current Limitations
- **Pie Chart Visualization** - Placeholder implementation
- **Market Data Integration** - Google Finance API pending
- **Real-time Updates** - Manual refresh required
- **Export Functionality** - Not yet implemented

### Workarounds
- Use Android Studio for full functionality
- Test on physical device for biometric features
- Ensure stable internet for market data

## 📞 Support

### Getting Help
1. **Check BUILD_INSTRUCTIONS.md** for build issues
2. **Review RELEASE_NOTES.md** for version information
3. **Submit GitHub Issues** for bug reports
4. **Contact development team** for technical support

### Common Issues
- **Build failures**: Check Android SDK installation
- **Installation issues**: Enable USB debugging
- **Biometric problems**: Test on physical device
- **Language issues**: Check system language settings

## 📄 License

This project is proprietary software. All rights reserved.

---

**Last Updated**: 2024  
**Version**: 0.1.1  
**Platform**: Android 6.0+ (API 23+)