# Release Notes

This document provides detailed information about each release of Wealth Manager, including new features, improvements, and bug fixes.

## 📱 Latest Release: v1.8.0

**Release Date**: January 15, 2025  
**Version Code**: 180  
**Target SDK**: Android 16 (API 36)

### 🎯 What's New

#### 🎯 **Total Asset Widget Development**
- Complete minimalist total asset widget implementation
- Enhanced widget privacy settings and configuration
- Improved widget syntax checking and validation
- Better widget performance and reliability
- Streamlined widget design for better user experience

#### 🔧 **CI/CD Improvements**
- Enhanced GitHub Actions workflow with Chinese prompts
- Improved error handling and security scanning
- Fixed GitHub Actions version compatibility issues
- Optimized CI scripts for better stability
- Enhanced build configuration management

#### 🛠️ **Developer Tools**
- Added comprehensive widget syntax checking script
- Enhanced build configuration and dependency management
- Improved error reporting and debugging capabilities
- Better development workflow and tooling

### 🔄 **Migration Guide**

No migration required for existing users. The app will automatically benefit from improved widget functionality and enhanced stability.

### 📋 **System Requirements**

- **Android**: 14+ (API 34+)
- **RAM**: 4GB minimum
- **Storage**: 100MB free space
- **Biometric**: Fingerprint or face recognition supported

---

## 📱 Release v1.6.0

**Release Date**: January 4, 2025  
**Version Code**: 160

### 🎯 What's New

#### 🔐 **Google Password Manager Integration**
- Enhanced credential management with Google Password Manager support
- Improved API key security and storage mechanisms
- Advanced biometric authentication integration
- Better credential management service implementation

#### 🎨 **Enhanced UI Components**
- New responsive dialog components for better user experience
- Improved accessibility features and audit capabilities
- Enhanced form validation with real-time feedback
- Better asset form validation and input field components

#### 🔄 **Sync and Backup Improvements**
- Advanced backup management with enhanced preferences
- Improved sync result handling and error management
- Better network utility functions for connectivity management
- Enhanced Wear OS sync capabilities

#### 🛠️ **Developer Experience**
- New onboarding flow components
- Enhanced debug logging and haptic feedback
- Improved security enhancement examples
- Better error handling and diagnostics

#### 📱 **UI/UX Enhancements**
- Updated dashboard and settings screens with improved layouts
- Enhanced asset management with better validation
- Improved API key management with better user guidance
- Better responsive design components

### 🔄 **Migration Guide**

No migration required for existing users. The app will automatically update your data to the new format.

### 📋 **System Requirements**

- **Android**: 14+ (API 34+)
- **RAM**: 4GB minimum
- **Storage**: 100MB free space
- **Biometric**: Fingerprint or face recognition supported

---

## 📱 Release v1.4.6

**Release Date**: January 31, 2025  
**Version Code**: 146

### 🎯 What's New

#### 🛠️ **Release Build Fixes**
- Fixed R8 obfuscation issues causing API query failures
- Added ProGuard rules for Retrofit/Gson compatibility
- Added `@SerializedName` annotations to DTOs

#### 🔇 **Logging Improvements**
- Disabled OkHttp BODY level logging in release builds
- Masked sensitive headers to prevent token leakage
- Improved debug logging for development

#### ✅ **API Integration**
- Restored normal API functionality in release builds
- Fixed market data query issues
- Improved API error handling

### 🔄 **Migration Guide**

No migration required. Existing users will benefit from improved API reliability.

---

## 📱 Release v1.4.5

**Release Date**: January 31, 2025  
**Version Code**: 145

### 🎯 What's New

#### 🚀 **Android 2025 Compliance**
- Updated to Android API 36 (Android 16)
- Implemented Splash Screen API for professional launch experience
- Added Edge-to-Edge support for immersive full-screen experience
- Added Android 13+ notification permission handling

#### 🔔 **Notification System**
- New `NotificationPermissionManager` component
- Support for Android 13+ notification permissions
- Integrated notification management in settings
- Complete notification permission workflow

#### 🎨 **UI/UX Improvements**
- Smooth app launch with no black screen flicker
- Immersive full-screen design
- Modern system bar behavior
- 120Hz optimized animations

#### 🌐 **Multi-language Support**
- Added notification permission strings in Chinese and English
- Complete accessibility content descriptions
- Improved localization coverage

### 🔄 **Migration Guide**

No migration required. The app will automatically request notification permissions when needed.

### 📋 **New Requirements**

- **Android**: 14+ (API 34+) required
- **Notification Permissions**: Required for Android 13+ devices

---

## 📱 Release v1.4.0

**Release Date**: January 31, 2025  
**Version Code**: 144

### 🎯 What's New

#### 🚀 **CI/CD Automation**
- Git Tag-based versioning (`vX.Y.Z` → `X.Y.Z`)
- Automated version code generation via GitHub Actions
- Automated release signing and AAB generation
- GitHub Release automation with artifacts

#### 🧭 **Responsive Design**
- Added `WindowSizeClass` for tablet and large screen support
- Optimized layouts for different screen sizes
- Improved user experience on tablets

#### 💵 **MoneyFormatter Utility**
- New `MoneyFormatter` module for consistent currency formatting
- Localized number formatting
- Improved currency display across the app

#### 🔐 **Enhanced Diagnostics**
- Improved `ApiDiagnostic` functionality
- Better network module observability
- Enhanced error reporting and debugging

### 🔄 **Migration Guide**

No migration required. Existing users will benefit from improved performance and new features.

### 📋 **Build Changes**

- **Gradle**: Now accepts `-PwmVersionName/-PwmVersionCode` parameters
- **CI Protection**: Release AAB only produced by CI
- **Version Management**: Automated version control

---

## 📱 Release v1.2.0

**Release Date**: January 30, 2025  
**Version Code**: 15

### 🎯 What's New

#### 🔍 **Stock Search Improvements**
- Added 450ms debounce for search requests
- Reduced minimum query length to 1 character
- Implemented immediate IME search functionality
- Improved search responsiveness

#### 🧹 **Request Management**
- Fixed stale search results using `flatMapLatest`
- Improved request cancellation
- Better search performance

### 🔄 **Migration Guide**

No migration required. Search functionality will be automatically improved.

---

## 📱 Release v1.1.0

**Release Date**: January 29, 2025  
**Version Code**: 14

### 🎯 What's New

#### 🌐 **Multi-language Support**
- Added English and Traditional Chinese language options
- Language switching in Settings page
- Automatic app restart when language changes
- Complete UI localization

#### 🎨 **UI/UX Improvements**
- Unified dynamic coloring across all interfaces
- Consistent theming with system preferences
- Improved language switching experience
- Direct app restart without user confirmation

#### 🐛 **Bug Fixes**
- Fixed dashboard stock price updates
- Corrected refresh button functionality
- Resolved dashboard data loading issues

#### 📝 **Code Quality**
- All comments and logs converted to English
- Unified code formatting and structure
- Improved error handling for locale management
- Enhanced application startup stability

### 🔄 **Migration Guide**

No migration required. Language settings will be preserved.

### 📋 **New Features**

- **Language Settings**: Available in Settings → Language
- **Automatic Restart**: App restarts automatically when language changes
- **Localized Content**: Complete Chinese and English support

---

## 📱 Release v1.0.0

**Release Date**: January 29, 2025  
**Version Code**: 13

### 🎯 What's New

#### 🔐 **Biometric Authentication System**
- Fingerprint and face recognition authentication
- 24-hour session timeout
- Secure session management
- Skip authentication option for accessibility

#### 💰 **Asset Management**
- Cash asset tracking (TWD and USD support)
- Stock portfolio management (Taiwan and US markets)
- Complete CRUD operations for assets
- Smart stock search with real-time symbol lookup
- Asset editing and deletion functionality

#### 📊 **Real-time Market Data**
- Finnhub API integration for global stocks
- TWSE API integration for Taiwan stock exchange
- Exchange Rate API for USD/TWD conversion
- Smart API failover and request deduplication
- Intelligent caching system with offline support
- Error recovery and retry mechanisms

#### 🎨 **Modern UI/UX**
- Jetpack Compose with Material 3 design
- Material You dynamic theming
- Responsive design for different screen sizes
- 120Hz performance optimization
- Multi-language support (English/Traditional Chinese)

#### ⚡ **Performance Features**
- Real-time performance monitoring
- Memory management optimization
- Smart cache strategy
- Request deduplication
- Background data refresh

#### 🗄️ **Data Management**
- Room database with local encrypted storage
- No cloud sync for complete privacy
- Secure data deletion
- Offline data availability

### 🔄 **Migration Guide**

This is the initial release, so no migration is required.

### 📋 **System Requirements**

- **Android**: 14+ (API 34+)
- **RAM**: 4GB minimum
- **Storage**: 100MB free space
- **Biometric**: Fingerprint or face recognition recommended
- **Internet**: Required for market data

### 🏆 **Key Features**

- **Security First**: Local-only storage with biometric authentication
- **Real-time Data**: Multi-API integration with smart failover
- **Modern UI**: Jetpack Compose with Material 3 design
- **Performance**: 120Hz optimization with intelligent caching
- **Privacy**: No cloud sync, complete data privacy

---

## 📱 Release v0.1.6

**Release Date**: January 28, 2025  
**Version Code**: 6

### 🎯 What's New

#### 🚀 **Play Store Preparation**
- Optimized for Google Play Store submission
- Enhanced UI components
- Improved performance
- Basic asset management functionality
- Market data integration
- Biometric authentication

### 🔄 **Migration Guide**

This is a pre-release version. Users should upgrade to v1.0.0 or later.

---

## 📱 Release v0.1.0

**Release Date**: January 28, 2025  
**Version Code**: 1

### 🎯 What's New

#### 🏗️ **Initial Setup**
- Basic Android application structure
- Core dependencies and configuration
- Initial project setup
- Basic development environment

### 🔄 **Migration Guide**

This is the initial development version. Users should upgrade to v1.0.0 or later.

---

## 🔄 **Version History Summary**

| Version | Release Date | Key Features | Status |
|---------|-------------|--------------|---------|
| 1.8.0   | 2025-01-15  | Total Asset Widget, CI/CD improvements, developer tools | ✅ Current |
| 1.6.0   | 2025-01-04  | Google Password Manager integration, enhanced UI | ✅ Supported |
| 1.4.7   | 2025-10-02  | API fixes, security enhancements | ✅ Supported |
| 1.4.6   | 2025-10-01  | Release build fixes, ProGuard rules | ✅ Supported |
| 1.4.5   | 2025-10-01  | Android 2025 compliance, Splash Screen | ✅ Supported |
| 1.4.0   | 2025-09-30  | CI/CD automation, responsive design | ✅ Supported |
| 1.2.0   | 2025-09-29  | Stock search improvements | ✅ Supported |
| 1.1.0   | 2025-09-29  | Multi-language support | ✅ Supported |
| 1.0.0   | 2025-09-29  | Complete feature set, production ready | ✅ Supported |
| 0.1.6   | 2025-09-28  | Play Store optimization | ❌ Deprecated |
| 0.1.0   | 2025-09-28  | Initial release | ❌ Deprecated |

## 🆘 **Support Information**

### **Getting Help**

- **Documentation**: [README.md](README.md)
- **Development Guide**: [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md)
- **API Setup**: [docs/API_SETUP.md](docs/API_SETUP.md)
- **Security**: [SECURITY.md](SECURITY.md)

### **Reporting Issues**

- **GitHub Issues**: [Report Bugs](https://github.com/kuoyaoming/Wealth-Manager/issues)
- **Security Issues**: [Security Policy](SECURITY.md)
- **Feature Requests**: [GitHub Discussions](https://github.com/kuoyaoming/Wealth-Manager/discussions)

### **Community**

- **Contributing**: [CONTRIBUTING.md](CONTRIBUTING.md)
- **Code of Conduct**: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)

---

**Last Updated**: October 2025  
**Next Release**: TBD

*For the complete changelog, see [CHANGELOG.md](CHANGELOG.md).*
