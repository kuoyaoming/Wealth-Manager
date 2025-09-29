# API Setup Guide

This guide explains how to configure API keys for the Wealth Manager application.

## 🔑 Required API Keys

### 1. Finnhub API
- **Purpose**: Stock market data and quotes
- **Registration**: https://finnhub.io/register
- **Free Tier**: Available
- **Usage**: US and international stock prices

### 2. Exchange Rate API
- **Purpose**: Currency exchange rates (USD/TWD)
- **Registration**: https://exchangerate-api.com/
- **Free Tier**: Available
- **Usage**: Real-time exchange rate conversion

## 🛠️ Setup Instructions

### Step 1: Get API Keys

1. **Register for Finnhub API**:
   - Visit https://finnhub.io/register
   - Create account and get API key
   - Free tier provides sufficient requests for development

2. **Register for Exchange Rate API**:
   - Visit https://exchangerate-api.com/
   - Create account and get API key
   - Free tier available

### Step 2: Configure API Keys

1. **Copy template file**:
   ```bash
   cp local.properties.template local.properties
   ```

2. **Edit local.properties**:
   ```properties
   sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   
   # API Keys
   FINNHUB_API_KEY=your_finnhub_api_key_here
   EXCHANGE_RATE_API_KEY=your_exchange_rate_api_key_here
   ```

3. **Test configuration**:
   ```bash
   # Run setup script
   .\docs\setup\setup-dev.ps1
   
   # Build project
   .\gradlew clean assembleDebug
   ```

## 🔐 Security Best Practices

### ✅ Do's
- Store API keys in `local.properties`
- Keep `local.properties` in `.gitignore`
- Use different keys for development and production
- Rotate API keys regularly
- Monitor API usage

### ❌ Don'ts
- Never commit API keys to version control
- Don't hardcode keys in source code
- Don't share API keys publicly
- Don't use production keys for development

## 🧪 Testing API Configuration

### Check BuildConfig
After building, verify in `app/build/generated/source/buildConfig/debug/com/wealthmanager/BuildConfig.java`:

```java
public static final String FINNHUB_API_KEY = "your_actual_key";
public static final String EXCHANGE_RATE_API_KEY = "your_actual_key";
```

### Test API Functionality
1. Launch the application
2. Try searching for stocks
3. Check if market data loads
4. Test currency conversion

## 🚨 Troubleshooting

### API Keys Not Loading
- Check `local.properties` format
- Ensure no extra spaces or special characters
- Verify file encoding is UTF-8
- Run `.\gradlew clean` and rebuild

### API Requests Failing
- Verify API keys are correct
- Check internet connection
- Review API usage limits
- Check API service status

### Build Errors
- Ensure `buildConfig true` in `build.gradle`
- Check Gradle version compatibility
- Clean and rebuild project

## 📞 Support

If you encounter issues:
1. Check [Security Policy](../security/SECURITY.md)
2. Review [Development Setup](../setup/README.md)
3. Create an issue on GitHub
