# Wealth Manager

A modern personal finance tracker built with Jetpack Compose. Secure, local-only asset management with real-time market data and biometric authentication.

## Features

- 🔐 **Biometric Security** - Fingerprint/face recognition
- 💰 **Portfolio Tracking** - Cash and stock investments
- 📊 **Real-time Data** - Live market prices via Alpha Vantage API
- 🎨 **Material You** - Dynamic theming
- 🌍 **i18n** - English & Traditional Chinese
- 📱 **Android 16** - Latest Android features

## Security

- **Local-only storage** - All data encrypted on device
- **Biometric authentication** - No passwords required
- **No cloud sync** - Complete privacy

## Asset Management

- **Cash tracking** - TWD and USD support
- **Stock portfolio** - Taiwan and US markets
- **Real-time prices** - Alpha Vantage API integration
- **Smart search** - Intelligent stock symbol matching

## Market Data

- **Alpha Vantage API** - Real-time stock prices and exchange rates
- **Auto-updates** - On app launch and manual refresh
- **Offline support** - Cached data when network unavailable

## Tech Stack

- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Repository Pattern
- **DI**: Hilt
- **Database**: Room
- **Networking**: Retrofit + OkHttp
- **Auth**: Android Biometric API
- **Language**: Kotlin
- **Target**: Android 16 (API 36)

## Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/wealth-manager.git

# Build the project
./gradlew assembleDebug

# Install on device
./gradlew installDebug
```

## Requirements

- Android 16+ (API 36)
- Biometric authentication (recommended)
- Internet connection for market data

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  Business Logic │    │   Data Layer    │
│                 │    │                 │    │                 │
│ • Compose UI    │◄──►│ • ViewModels    │◄──►│ • Repository    │
│ • Navigation    │    │ • Use Cases     │    │ • Room DB       │
│ • Material 3    │    │ • Managers      │    │ • API Service   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

MIT License - see [LICENSE](LICENSE) file for details.

---

**Version**: 0.1.10  
**Last Updated**: 2025  
**Android Support**: 16+ (API 36)  
**Build**: Production Ready