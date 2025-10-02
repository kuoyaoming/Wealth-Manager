# Wealth Manager

Modern Android personal finance tracker powered by Jetpack Compose. Local-only data storage, biometric authentication, real‑time market data (Finnhub, TWSE, ExchangeRate‑API), multilingual UI, 120Hz optimizations, and rich haptics. **Now fully compliant with Android 2025 official design guidelines!**

Language: [English](README.md) | [繁體中文](README_zh.md)

## Features

- 🔐 **Biometric Security**: Fingerprint/face authentication with 24‑hour session timeout
- 💰 **Portfolio Tracking**: Cash and stock management with full CRUD
- 📊 **Real‑time Market Data**: Finnhub (global), TWSE (Taiwan), ExchangeRate‑API (USD/TWD)
- 🔁 **Resilience Built‑in**: Failover, retry, request deduplication, and offline cache
- 🎨 **Material You**: Dynamic color with responsive layouts
- ⚙️ **Performance**: 120Hz‑friendly UI and performance monitoring
- 🌍 **Localization**: English and Traditional Chinese, instant switching
- ⌚ **Wear OS**: Companion sync via data layer (separate Wear module)
- 🚀 **Android 2025 Compliant**: Latest API 36, Splash Screen, Edge-to-Edge, Notification permissions
- 🔔 **Smart Notifications**: Android 13+ notification permission management

## 📱 Screenshots

<div align="center">

### Portfolio Overview & Asset Management
<table>
<tr>
<td width="50%">
<img src="docs/screenshots/portfolio-overview.png" alt="Portfolio Overview" width="100%"/>
<p align="center"><em>Portfolio overview with distribution and real‑time valuation</em></p>
</td>
<td width="50%">
<img src="docs/screenshots/asset-management.png" alt="Asset Management" width="100%"/>
<p align="center"><em>Manage cash and stocks with an intuitive workflow</em></p>
</td>
</tr>
</table>

### Biometric Authentication & Add Assets
<table>
<tr>
<td width="50%">
<img src="docs/screenshots/biometric-auth.png" alt="Biometric Authentication" width="100%"/>
<p align="center"><em>Secure sign‑in with clear privacy notice</em></p>
</td>
<td width="50%">
<img src="docs/screenshots/add-assets.png" alt="Add Assets" width="100%"/>
<p align="center"><em>Smart add flow with stock search and symbol lookup</em></p>
</td>
</tr>
</table>

### About & Privacy
<table>
<tr>
<td width="50%">
<img src="docs/screenshots/about-app.png" alt="About Wealth Manager" width="100%"/>
<p align="center"><em>Transparent privacy and third‑party API disclosure</em></p>
</td>
<td width="50%">
<!-- Empty cell for balanced layout -->
</td>
</tr>
</table>

</div>

## Security & Privacy

- **Local‑only storage**: Financial data stays on the device (no cloud sync)
- **Biometric authentication**: No passwords, 24‑hour session timeout
- **API keys encrypted on‑device**: Stored with EncryptedSharedPreferences
- **Logging hygiene**: Keys redacted; diagnostics focus on non‑sensitive details

Docs: [Security Policy](docs/security/SECURITY.md) · [API Setup](docs/api/API_SETUP.md)

## Getting Started

```bash
# Clone the repository (or your fork)
git clone https://github.com/kuoyaoming/Wealth-Manager.git

# Build (Debug)
./gradlew -PwmVersionName=0.0.0-beta.local -PwmVersionCode=1 assembleDebug

# Install on device
./gradlew installDebug
```

Requirements
- Android 14+ (API 34+)
- Target SDK 35 (Android 15)
- Biometric hardware recommended
- Internet connection for market data

## API Keys

- Configure inside the app: Settings → Manage API Keys → Validate & Save
- Keys are stored encrypted on‑device; do not commit keys to source control
- No BuildConfig keys; avoid placing real keys in `local.properties`
- Detailed steps: [docs/api/API_SETUP.md](docs/api/API_SETUP.md)
 - Detailed steps: [API Setup (English)](docs/api/API_SETUP.md) · [繁體中文](docs/api/API_SETUP_zh.md)

## Architecture Overview

- **Pattern**: MVVM + Repository
- **Data**: Room (local storage), Retrofit/OkHttp
- **DI**: Hilt
- **UI**: Jetpack Compose + Material 3
- **Security**: EncryptedSharedPreferences for API keys; biometric auth

```mermaid
flowchart TB
  subgraph UI_Layer
    UI1[Compose screens]
    UI2[Navigation]
    UI3[Material 3 and responsive]
    UI4[Haptics and 120Hz UX]
  end

  subgraph Business_Logic
    BL1[ViewModels]
    BL2[MarketDataService]
    BL3[Auth state]
    BL4[FirstLaunchManager]
  end

  subgraph Data_Layer
    DL1[Repositories]
    DL2[Room database]
    DL3[API provider]
    DL4[Caching and resilience]
  end

  UI1 --> BL1
  UI2 --> BL1
  UI3 --> BL1
  UI4 --> BL1

  BL1 --> DL1
  BL2 --> DL1
  BL3 --> DL1

  DL1 --> DL2
  DL1 --> DL3
  DL1 --> DL4
```

### Core Modules

```mermaid
flowchart LR
  subgraph Auth
    A1[BiometricAuthManager]
    A2[AuthStateManager]
    A3[BiometricAuthScreen]
  end

  subgraph Assets
    B1[AssetsScreen]
    B2[AssetRepository]
    B3[Room DAOs]
  end

  subgraph Market_Data
    C1[MarketDataService]
    C2[ApiProviderService]
    C3[Cache and parser]
    C4[Resilience]
  end

  subgraph UI_and_System
    D1[DashboardScreen]
    D2[SettingsScreen]
    D3[Navigation]
    D4[Material 3 and responsive]
    D5[PerformanceMonitor120Hz]
  end

  A1 --> A3
  A2 --> A3
  A3 --> D1
  D1 --> B1
  B1 --> C1
  C1 --> B2
  B2 --> B3
```

## Data Flow

```mermaid
flowchart TD
  UI[Compose UI] --> VM[ViewModel]
  VM --> REPO[Repository]
  REPO --> DB[Room]
  REPO --> API[ApiProviderService]

  API --> F[Finnhub]
  API --> T[TWSE]
  API --> X[ExchangeRate]

  F --> C[Cache]
  T --> C
  X --> C

  C --> MDS[MarketDataService]
  MDS --> AU[Asset updates]
  AU --> DB

  AUTH[Biometric and auth state] --> VM
  K[KeyRepository] --> API
  R[Resilience] --> API
  OFF[Offline mode] --> C
  C --> L[Local cache] --> UI
```

## Tech Stack

- Kotlin, Jetpack Compose, Material 3
- Hilt, Room (local storage), Retrofit, OkHttp (logging)
- AndroidX Biometric
- Coroutines/Flows
- Wear OS data layer

## Wear OS

- Separate module `wear` (`minSdk 30`, `targetSdk 35`)
- Companion sync via `MobileWearSyncService` using Play Services Wearable

## Contributing

See [docs/development/CONTRIBUTING.md](docs/development/CONTRIBUTING.md)

## License

MIT License — see [LICENSE](LICENSE)

---

Version: 1.4.7  
Last Updated: 2025  
Min SDK: 34 (Android 14)  
Target SDK: 35 (Android 15)

## Release & Versioning

- versionName: Derived from Git tag `vX.Y.Z` → `X.Y.Z` (SemVer)
- versionCode: Assigned by CI with `GITHUB_RUN_NUMBER`
- Release AAB: Produced only on CI when a tag is pushed

Examples
```bash
# Create and push release tag
git tag v1.4.7
git push origin v1.4.7

# Local debug build (release bundles are CI‑only)
./gradlew -PwmVersionName=0.0.0-beta.local -PwmVersionCode=1 assembleDebug
```

### Navigation Graph

```mermaid
flowchart TD
  A[auth] -->|onAuthSuccess| B[dashboard]
  A -->|onSkipAuth when not authenticated| B
  B -->|onNavigateToAssets| C[assets]
  B -->|onNavigateToSettings| D[settings]
  C -->|onNavigateBack| B
  D -->|onNavigateBack| B
```

Artifacts
- On tag, CI builds `:app:bundleRelease` and uploads `.aab` and `mapping.txt`

## Development Status

### ✅ Completed
- Biometric auth with 24‑hour session timeout
- Cash/stock asset management (CRUD)
- Real‑time market data (Finnhub, TWSE, ExchangeRate‑API)
- Failover, retry, request deduplication
- Responsive UI with Material 3
- English/Traditional Chinese localization
- Performance monitoring and 120Hz optimizations
- Smart caching with offline support
- Error recovery & diagnostics

### 🚧 In Development
- Portfolio charts
- Advanced analytics
- Data export
- Enhanced chart components