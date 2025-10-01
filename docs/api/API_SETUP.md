# API Setup Guide

Language: [English](API_SETUP.md) | [繁體中文](API_SETUP_zh.md)

Configure API keys for Wealth Manager using the in‑app settings. Keys are stored locally and encrypted.

## Required API Keys

### Finnhub
- Purpose: Stock market data and quotes
- Registration: https://finnhub.io/register
- Free tier: Available

### ExchangeRate‑API
- Purpose: Currency rates (USD/TWD)
- Registration: https://www.exchangerate-api.com/
- Free tier: Available

## Setup Instructions (In‑App)

1) Obtain keys
- Register and get API keys from the providers above.

2) Add keys in the app
- Open the app → Settings → Manage API Keys → Paste keys → Validate & Save.
- The app validates keys with the provider and stores them using EncryptedSharedPreferences via `KeyRepository`.

3) Verify data
- Go to Dashboard or Assets to confirm market data and rates load successfully.

Notes
- No BuildConfig keys. Do not put keys in `local.properties`.
- Keys never leave the device. They are encrypted on‑device and not backed up to cloud.
- Logs redact secrets. UI shows masked previews only.

## Security Best Practices

Do
- Store keys only in the app (Settings → Manage API Keys).
- Use different keys for development and production.
- Rotate keys regularly and monitor usage.
- Keep original keys in a password manager.

Don’t
- Don’t commit keys to version control.
- Don’t hardcode keys in source code.
- Don’t place keys in `local.properties` or any config files.
- Don’t share keys in screenshots or logs.

## Testing API Configuration

Validate in app
- Use “Validate & Save” in Settings → Manage API Keys.
- On success, you should see up‑to‑date quotes and exchange rates.

Optional diagnostics
- Check the in‑app diagnostics/logs (sensitive parts are masked).

## Troubleshooting

Keys not accepted
- Reopen Manage API Keys, clear and re‑paste the keys.
- Confirm the key format with the provider; check account limits/plan.
- Ensure device time and network are correct.

Requests failing
- Verify keys are valid and not rate‑limited.
- Check internet connectivity and provider status pages.
- Retry Validate in Settings.

Build issues
- Clean and rebuild (`./gradlew clean assembleDebug`).
- Verify Android Gradle Plugin and dependency versions match the project.

## Support

If you encounter issues:
1) See [Security Policy](../security/SECURITY.md)
2) Review [Development Setup](../setup/README.md)
3) Create an issue on GitHub
