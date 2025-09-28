# Wealth Manager - Build Instructions

## Prerequisites

1. **Android Studio** (Latest version recommended)
2. **Android SDK** (API 23+ for minimum, API 34+ for target)
3. **Java 8** or higher
4. **Gradle 8.0+**

## Setup Instructions

### 1. Clone and Setup
```bash
git clone <repository-url>
cd Wealth-Manager
```

### 2. Configure Android SDK
1. Open Android Studio
2. Go to File → Settings → Appearance & Behavior → System Settings → Android SDK
3. Install the following:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools
   - Android SDK Tools

### 3. Create local.properties
Create a `local.properties` file in the root directory:
```properties
sdk.dir=/path/to/your/android/sdk
```

### 4. Build the Project
```bash
./gradlew build
```

### 5. Run the App
```bash
./gradlew installDebug
```

## Features Implemented

### ✅ Completed Features
- **Biometric Authentication**: Secure app access with fingerprint/face recognition
- **Material You Theming**: Dynamic color theming based on system settings
- **Multi-language Support**: Auto-detection of Chinese/English system language
- **Local Database**: Room database for secure local data storage
- **Asset Management**: Cash and stock asset tracking
- **Navigation**: Clean navigation between dashboard and assets
- **Responsive UI**: Modern Material 3 design

### 🚧 In Progress Features
- **Market Data Integration**: Google Finance API integration
- **Pie Chart Visualization**: Interactive asset distribution charts
- **Real-time Updates**: Live market data and exchange rates

## Project Structure

```
app/
├── src/main/java/com/wealthmanager/
│   ├── auth/                    # Biometric authentication
│   ├── data/                    # Database and repository layer
│   │   ├── entity/             # Room entities
│   │   ├── dao/                 # Data access objects
│   │   ├── database/           # Database configuration
│   │   └── repository/          # Repository pattern
│   ├── di/                      # Dependency injection (Hilt)
│   ├── ui/                      # UI layer
│   │   ├── auth/                # Authentication screens
│   │   ├── dashboard/           # Dashboard screens
│   │   ├── assets/              # Asset management screens
│   │   ├── navigation/          # Navigation setup
│   │   └── theme/               # Material You theming
│   └── MainActivity.kt          # Main activity
├── src/main/res/
│   ├── values/                  # English strings
│   ├── values-zh-rTW/           # Traditional Chinese strings
│   ├── drawable/                # Icons and images
│   └── mipmap-*/                # App icons
└── build.gradle                 # App-level build configuration
```

## Security Features

- **Local Storage Only**: All data stored locally, no cloud sync
- **Biometric Protection**: App access requires biometric authentication
- **Data Encryption**: Room database with encryption support
- **No Network Permissions**: Except for market data (optional)

## Development Notes

### Dependencies Used
- **Jetpack Compose**: Modern UI toolkit
- **Material 3**: Latest Material Design components
- **Room Database**: Local data persistence
- **Hilt**: Dependency injection
- **Navigation Compose**: Type-safe navigation
- **Biometric**: Android biometric authentication
- **Retrofit**: Network requests (for market data)

### Architecture
- **MVVM Pattern**: Model-View-ViewModel architecture
- **Repository Pattern**: Clean data access layer
- **Dependency Injection**: Hilt for dependency management
- **Reactive Programming**: Kotlin Flow for data streams

## Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Troubleshooting

### Common Issues

1. **Build Failures**: Ensure Android SDK is properly configured
2. **Biometric Issues**: Test on physical device (emulator may not support biometrics)
3. **Language Issues**: Check device language settings
4. **Database Issues**: Clear app data and reinstall

### Debug Mode
Enable debug logging in `build.gradle`:
```gradle
android {
    buildTypes {
        debug {
            buildConfigField "boolean", "DEBUG_MODE", "true"
        }
    }
}
```

## Contributing

1. Follow the existing code style
2. Add tests for new features
3. Update documentation
4. Ensure all features work in both languages

## License

This project is proprietary software. All rights reserved.