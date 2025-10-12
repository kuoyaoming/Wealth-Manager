# Wealth Manager

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build Status](https://github.com/kuoyaoming/Wealth-Manager/actions/workflows/release.yml/badge.svg)](https://github.com/kuoyaoming/Wealth-Manager/actions)

A modern, privacy-first personal finance tracker for Android and Wear OS. Built with Jetpack Compose, Kotlin, and the latest Android technologies.

---

## Table of Contents
- [Features](#features)
- [Getting Started](#getting-started)
- [Architecture](#architecture)
- [Modules](#modules)
- [Security](#security)
- [Localization](#localization)
- [Contributing](#contributing)
- [License](#license)
- [Support](#support)

---

## Features
- **Local-Only Data Storage**: All financial data is encrypted and stored on your device. No cloud sync, no analytics, no third-party sharing.
- **Biometric Authentication**: Secure access using fingerprint or face recognition.
- **Asset Management**: Track cash (TWD, USD) and stock portfolios. CRUD operations for assets.
- **Market Data Integration**: Real-time prices via Finnhub, TWSE, and ExchangeRate-API. Smart failover and caching for offline use.
- **120Hz Performance**: Optimized for high refresh rate devices.
- **Multi-Language Support**: English and Traditional Chinese. Instant switching, full localization.
- **Wear OS Companion**: Standalone Wear OS app with data sync, tiles, and haptic feedback.
- **Modern UI/UX**: Material 3, responsive layouts, edge-to-edge design.

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+
- Android SDK API 34+ (Android 14+)
- Kotlin 1.9.0+

### Build & Run
```sh
git clone https://github.com/kuoyaoming/Wealth-Manager.git
cd Wealth-Manager
# Configure Android SDK
# echo "sdk.dir=/path/to/android/sdk" > local.properties
./gradlew assembleDebug
./gradlew installDebug
```

### API Keys
- API keys are managed via app settings (Settings → Manage API Keys).
- No hardcoded API keys; keys are encrypted and stored locally.
- Supported APIs: Finnhub, TWSE, ExchangeRate-API.

---

## Architecture
- **MVVM + Repository Pattern**: Separation of UI, business logic, and data layers.
- **Dependency Injection**: Hilt for scalable, testable code.
- **Database**: Room with encryption.
- **Networking**: Retrofit + OkHttp.
- **Authentication**: Android Biometric API.
- **Async**: Kotlin Coroutines and Flow.

---

## Modules
- `app/` — Main Android application
- `wear/` — Wear OS companion app
- `docs/` — Documentation, setup scripts, contributing guides
- `.github/` — CI/CD workflows, issue templates

---

## Security
- All sensitive data is encrypted using Android Keystore and EncryptedSharedPreferences.
- No hardcoded secrets in source code.
- API keys are never stored in version control.
- HTTPS-only network communication.

---

## Localization
- English and Traditional Chinese
- Currency formatting and number systems
- Accessibility and content descriptions

---

## Contributing
We welcome contributions! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## License
MIT License. See [LICENSE](LICENSE) for details.

---

## Support
- Documentation: [docs/README.md](docs/README.md)
- API Setup: [docs/API_SETUP.md](docs/API_SETUP.md)
- Security: [SECURITY.md](SECURITY.md)
- Issues: [GitHub Issues](https://github.com/kuoyaoming/Wealth-Manager/issues)

---

**Wealth Manager** — Secure, private, and modern personal finance tracking for Android and Wear OS.
