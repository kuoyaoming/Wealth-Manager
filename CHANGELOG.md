# Changelog

All notable changes to the Wealth Manager project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- GitHub Issues and Pull Request templates
- Code of Conduct
- Changelog documentation

## [1.8.0] - 2025-01-15

### Added
- 🎯 **Total Asset Widget Development**:
  - Complete minimalist total asset widget implementation
  - Enhanced widget privacy settings and configuration
  - Improved widget syntax checking and validation
  - Better widget performance and reliability
- 🔧 **CI/CD Improvements**:
  - Enhanced GitHub Actions workflow with Chinese prompts
  - Improved error handling and security scanning
  - Fixed GitHub Actions version compatibility issues
  - Optimized CI scripts for better stability
- 🛠️ **Developer Tools**:
  - Added comprehensive widget syntax checking script
  - Enhanced build configuration and dependency management
  - Improved error reporting and debugging capabilities

### Changed
- 📱 **Widget System**:
  - Streamlined total asset widget design
  - Enhanced privacy controls for widget data
  - Improved widget rendering performance
- 🔧 **Build System**:
  - Updated GitHub Actions dependencies and workflows
  - Enhanced CI/CD pipeline stability
  - Improved build configuration management

### Fixed
- 🐛 **CI/CD Issues**:
  - Resolved GitHub Actions version and logic issues
  - Fixed actions/upload-artifact version compatibility
  - Corrected workflow name field case sensitivity
  - Enhanced error handling in CI processes
- 🛠️ **Build Configuration**:
  - Fixed dependency version conflicts
  - Improved build stability and reliability
  - Enhanced error reporting mechanisms

### Dependencies
- 📦 Updated GitHub Actions dependencies for better compatibility
- 📦 Enhanced security scanning and error handling
- 📦 Improved build tool configuration

## [1.6.0] - 2025-01-04

### Added
- 🔐 **Google Password Manager Integration**:
  - Enhanced credential management with Google Password Manager support
  - Improved API key security and storage mechanisms
  - Advanced biometric authentication integration
- 🎨 **Enhanced UI Components**:
  - New responsive dialog components for better user experience
  - Improved accessibility features and audit capabilities
  - Enhanced form validation with real-time feedback
- 🔄 **Sync and Backup Improvements**:
  - Advanced backup management with enhanced preferences
  - Improved sync result handling and error management
  - Better network utility functions for connectivity management
- 🛠️ **Developer Experience**:
  - New onboarding flow components
  - Enhanced debug logging and haptic feedback
  - Improved security enhancement examples

### Changed
- 📱 **UI/UX Enhancements**:
  - Updated dashboard and settings screens with improved layouts
  - Enhanced asset management with better validation
  - Improved API key management with better user guidance
- 🔧 **Technical Improvements**:
  - Updated security mechanisms and key repository
  - Enhanced backup and sync management
  - Improved error handling and diagnostics

### Fixed
- 🐛 **Bug Fixes**:
  - Resolved various UI component issues
  - Fixed API key management problems
  - Improved error handling across the application
- 🔧 **Code Quality**:
  - Updated detekt baseline files
  - Improved code formatting and structure
  - Enhanced error handling mechanisms

## [1.5.2] - 2025-01-27

### Added
- 🎯 Enhanced Wear OS design compliance with SDK guidelines
- 🔄 Optimized mobile-wear synchronization with retry mechanisms
- 📊 Improved data caching and performance optimization
- 🛠️ Added comprehensive error handling for sync operations

### Fixed
- 🔧 Fixed WearSyncManager compilation errors and indentation issues
- 🛠️ Resolved JaCoCo configuration conflicts in build system
- 📱 Improved Wear OS tile layout using PrimaryLayout.Builder
- ⚡ Enhanced sync debouncing and connection management

### Changed
- 🎨 Updated Wear OS UI components with proper font sizes and spacing
- 🔄 Implemented exponential backoff retry mechanism for sync operations
- 📊 Added smart caching strategy to reduce unnecessary data updates
- 🛡️ Enhanced data validation for sync operations

### Performance
- ⚡ Optimized Wear OS tile rendering performance
- 🔄 Reduced sync frequency with intelligent debouncing
- 📊 Improved data validation and error handling
- 🛠️ Enhanced connection state management

### Dependencies
- 📦 Updated GitHub Actions dependencies (checkout@v5, setup-java@v5, wrapper-validation@v3)
- 📦 Updated Wear OS Compose dependencies (1.3.0 → 1.5.2)
- 📦 Updated Wear OS Tiles dependencies (1.3.0 → 1.5.0)

### CI/CD
- 🚫 Temporarily disabled quality checks and coverage requirements (by user request)
- 🔧 Fixed build configuration issues
- ✅ Improved CI pipeline stability

## [1.4.7] - 2025-10-02

### Fixed
- 🔧 Fixed API key management issues
- 🛡️ Enhanced security mechanisms
- 📱 Improved user experience and interface stability

### Security
- 🔒 Enhanced API key encryption storage
- 🔒 Improved biometric authentication flow
- 🔒 Strengthened data protection mechanisms

### Performance
- ⚡ Optimized app startup speed
- ⚡ Improved memory usage efficiency
- ⚡ Enhanced overall system stability

### Code Quality
- 🧹 Fixed detekt configuration issues
- 🧹 Removed duplicate excludeClassPattern in detekt.yml
- 🧹 Updated detekt.yml configuration to resolve validation errors
- 🧹 Comprehensive detekt code quality improvements

## [1.4.6] - 2025-10-01

### Fixed
- 🛠️ Fixed release version API query failures: R8 obfuscation causing Gson field mapping failures
- 🛡️ Added ProGuard rules for Retrofit/Gson, added @SerializedName to DTOs
- 🔇 Disabled OkHttp BODY level logging in release and masked sensitive headers to prevent token leakage

### Notes
- Testing and settings page "API Key Test" consistent, actual search/quotes restored to normal

## [1.4.5] - 2025-10-01

### Added
- 🚀 **Android 2025 Official Design Guidelines Compliance**:
  - Updated to Android API 36 (compileSdk/targetSdk 36)
  - Implemented Splash Screen API for professional launch experience
  - Complete Edge-to-Edge implementation with immersive full-screen experience
  - Added Android 13+ notification permission handling
- 🔔 **Notification System**:
  - Added `NotificationPermissionManager` component
  - Support for Android 13+ notification permission requests
  - Integrated into settings page with complete notification management
- 🎨 **UI/UX Improvements**:
  - Smooth app launch screen, eliminating black screen flicker
  - Immersive full-screen design with content extending to status bar
  - Modern system bar behavior configuration
- 🌐 **Multi-language Support**:
  - Added notification permission related Chinese and English strings
  - Complete accessibility design content descriptions

### Changed
- 📱 **Target Platform**: minSdk 34 → 34, targetSdk 35 → 36, compileSdk 35 → 36
- 🏗️ **Build Configuration**: Added Splash Screen dependency `androidx.core:core-splashscreen:1.0.1`
- 🎯 **Permission Declaration**: Added Android 13+ notification permission declaration
- 🔧 **Code Fixes**: Fixed @Composable issues in TreemapChartComponent

### Fixed
- 🐛 Fixed @Composable calling issues in TreemapChartComponent
- 🐛 Fixed assignment issues in semantics blocks
- 🐛 Ensured all improvements compile and run normally

### Security
- 🔒 Compliant with 2025 Android official security guidelines
- 🔒 Support for latest Android security mechanisms
- 🔒 Complete notification permission management

### Performance
- ⚡ Optimized app startup performance
- ⚡ Improved full-screen rendering performance
- ⚡ Better memory management

## [1.4.0] - 2025-09-30

### Added
- 🚀 **CI Version Control and Release Process**:
  - versionName determined by Git Tag (`vX.Y.Z` → `X.Y.Z`)
  - versionCode automatically generated by GitHub Actions `GITHUB_RUN_NUMBER` (strictly incremental)
  - Tag triggers signed `:app:bundleRelease` and `mapping.txt` output
- 🧭 **Responsive Layout**: Added `WindowSizeClass` for optimized tablet and large screen experience
- 💵 **MoneyFormatter Utility Module**: Consistent amount formatting and localized display
- 🔐 **Security/Diagnostics**: Optimized `ApiDiagnostic` and network module observability

### Changed
- 📦 **Gradle**: `app/build.gradle` now receives `-PwmVersionName/-PwmVersionCode`, avoiding git calls during configuration, added non-CI release AAB protection
- 🧱 **Build/Targets**: minSdk 34, targetSdk 35, compileSdk 35, Build Tools 36.1.0
- 🧭 **Dashboard/Charts**: Optimized reorganization and memory allocation, improved performance and smoothness
- 🌐 **Multi-language**: Optimized settings page and language switching flow, compliant with 2025 per-app-language guidelines

### Fixed
- 🛠️ Fixed market data query stability and error handling; improved recovery capability under network exception scenarios
- 🧹 Fixed state synchronization and logic boundary conditions for some UI components

### Notes
- Official AAB release only produced by CI; local `bundleRelease` prohibited

## [1.2.0] - 2025-09-29

### Changed
- Version bump for app and wear modules: versionCode 15, versionName 1.2.0
- Improved stock search UX: debounce 450ms, min query length 1, immediate IME search

### Fixed
- Cancelled outdated stock search requests using flatMapLatest to prevent stale results

## [1.1.0] - 2025-09-29

### Added
- 🌐 **Multi-language Support**
  - English and Traditional Chinese language options
  - Language switching in Settings page
  - Automatic app restart when language changes
  - Localized UI strings and content descriptions

### Changed
- 🔧 **Language Management**
  - Unified dynamic coloring across all interfaces
  - Consistent theming with system preferences
  - Improved language switching user experience
  - Direct app restart without user confirmation

### Fixed
- 🐛 **Dashboard Stock Price Updates**
  - Fixed stock price query functionality on homepage
  - Corrected refresh button to trigger market data updates
  - Resolved dashboard data loading issues

### Technical Improvements
- 📝 **Code Quality**
  - All comments and logs converted to English
  - Unified code formatting and structure
  - Improved error handling for locale management
  - Enhanced application startup stability

## [1.0.0] - 2025-09-29

### Added
- 🔐 **Biometric Authentication System**
  - Fingerprint/face recognition authentication
  - 24-hour session timeout
  - Secure session management
  - Skip authentication option

- 💰 **Asset Management**
  - Cash asset tracking (TWD and USD support)
  - Stock portfolio management (Taiwan and US markets)
  - Complete CRUD operations for assets
  - Smart stock search with real-time symbol lookup
  - Asset editing and deletion functionality

- 📊 **Real-time Market Data**
  - Finnhub API integration for US and international stocks
  - TWSE API integration for Taiwan stock exchange
  - Exchange Rate API for USD/TWD conversion
  - Smart API failover and request deduplication
  - Intelligent caching system with offline support
  - Error recovery and retry mechanisms

- 🎨 **Modern UI/UX**
  - Jetpack Compose with Material 3 design
  - Material You dynamic theming
  - Responsive design for different screen sizes
  - 120Hz performance optimization
  - Multi-language support (English/Traditional Chinese)

- ⚡ **Performance Features**
  - Real-time performance monitoring
  - Memory management optimization
  - Smart cache strategy
  - Request deduplication
  - Background data refresh

- 🗄️ **Data Management**
  - Room database with local encrypted storage
  - No cloud sync for complete privacy
  - Secure data deletion
  - Offline data availability

- 🔧 **Developer Experience**
  - Comprehensive documentation
  - Security best practices
  - API setup guides
  - Development environment scripts
  - Contributing guidelines

### Technical Details
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Hilt
- **Database**: Room with encryption
- **Networking**: Retrofit + OkHttp
- **Authentication**: Android Biometric API
- **Target SDK**: Android 16 (API 36)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose

### Security Features
- Local-only data storage with encryption
- No personal data collection
- No analytics tracking
- No third-party data sharing
- Secure API key management
- HTTPS-only communications

## [0.1.6] - 2025-09-28

### Added
- Initial release preparation
- Basic asset management functionality
- Market data integration
- Biometric authentication

### Changed
- Optimized for Google Play Store
- Enhanced UI components
- Improved performance

## [0.1.0] - 2025-09-28

### Added
- Initial project setup
- Basic Android application structure
- Core dependencies and configuration

---

## Version History

| Version | Release Date | Key Features |
|---------|-------------|--------------|
| 1.4.7   | 2025-10-02  | API key management fixes, security enhancements, performance improvements |
| 1.4.6   | 2025-10-01  | R8 obfuscation fixes, ProGuard rules, release optimization |
| 1.4.5   | 2025-10-01  | Android 2025 compliance, Splash Screen, Edge-to-Edge, notification permissions |
| 1.4.0   | 2025-09-30  | CI-driven versioning, responsive layout, MoneyFormatter |
| 1.2.0   | 2025-09-29  | Stock search UX improvements, debounce optimization |
| 1.1.0   | 2025-09-29  | Multi-language support, improved UX |
| 1.0.0   | 2025-09-29  | Complete feature set, production ready |
| 0.1.6   | 2025-09-28  | Play Store optimization |
| 0.1.0   | 2025-09-28  | Initial release |

## Release Notes

### v1.0.0 - Production Ready
This is the first major release of Wealth Manager, featuring a complete personal finance tracking application with biometric security, real-time market data, and modern Android development practices.

### Key Highlights
- **Security First**: Local-only storage with biometric authentication
- **Real-time Data**: Multi-API integration with smart failover
- **Modern UI**: Jetpack Compose with Material 3 design
- **Performance**: 120Hz optimization with intelligent caching
- **Privacy**: No cloud sync, complete data privacy

### Migration Guide
This is the initial release, so no migration is required.

### Breaking Changes
None - this is the initial release.

### Deprecations
None - this is the initial release.

---

**Maintainer**: Wealth Manager Team  
**Last Updated**: October 2025  
**Next Release**: TBD