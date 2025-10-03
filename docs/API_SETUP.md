# API Setup Guide

This guide explains how to configure API keys for Wealth Manager to enable real-time market data and currency conversion features.

## 📋 Table of Contents

- [Overview](#overview)
- [Required APIs](#required-apis)
- [API Key Setup](#api-key-setup)
- [Configuration](#configuration)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## 🔍 Overview

Wealth Manager integrates with multiple APIs to provide real-time market data:

- **Finnhub API**: Global stock market data
- **TWSE API**: Taiwan stock exchange data
- **ExchangeRate-API**: Currency conversion rates

All API keys are stored securely on your device using Android Keystore encryption.

## 🔑 Required APIs

### **1. Finnhub API**

**Purpose**: Global stock market data for US and international stocks

**Features**:
- Real-time stock prices
- Company information
- Market data for global exchanges

**Sign Up**: [Finnhub.io](https://finnhub.io/register)
- Free tier: 60 API calls/minute
- Paid plans available for higher limits

**API Key Format**: `your_finnhub_key_here`

### **2. TWSE API**

**Purpose**: Taiwan stock exchange data

**Features**:
- Taiwan stock prices
- TWSE market data
- Taiwan company information

**Sign Up**: [TWSE API](https://www.twse.com.tw/en/page/help/api.html)
- Free tier available
- Registration required

**API Key Format**: `your_twse_key_here`

### **3. ExchangeRate-API**

**Purpose**: Currency conversion rates (USD/TWD)

**Features**:
- Real-time exchange rates
- Historical rate data
- Multiple currency support

**Sign Up**: [ExchangeRate-API](https://exchangerate-api.com/)
- Free tier: 1,500 requests/month
- Paid plans for higher limits

**API Key Format**: `your_exchange_rate_key_here`

## ⚙️ API Key Setup

### **Method 1: In-App Configuration (Recommended)**

1. **Open Wealth Manager**
2. **Go to Settings** → **Manage API Keys**
3. **Enter your API keys** in the respective fields
4. **Click "Validate & Save"**
5. **Keys are encrypted and stored securely**

### **Method 2: Environment Configuration (Development)**

1. **Configure Android SDK**:
   ```bash
   # Option 1: Set environment variable
   export ANDROID_HOME=/path/to/android/sdk
   
   # Option 2: Create local.properties (SDK path only)
   echo "sdk.dir=/path/to/android/sdk" > local.properties
   ```

2. **API Keys**: Configure through the app's settings interface - no build configuration needed.

3. **Build and run**:
   ```bash
   ./gradlew assembleDebug
   ```

### **Method 3: Build Configuration (CI/CD)**

Set environment variables in your CI/CD system:

API keys are now managed through the app's settings interface. No environment variables needed.

## 🔧 Configuration

### **API Key Validation**

The app automatically validates API keys when you save them:

- **Finnhub**: Tests connection and retrieves sample data
- **TWSE**: Validates API key format and connection
- **ExchangeRate-API**: Tests currency conversion functionality

### **Error Handling**

If API keys are invalid or missing:

- **Warning messages** will be displayed
- **Market data features** will be disabled
- **App will continue to work** with limited functionality

### **Security Features**

- **Encrypted Storage**: All API keys are encrypted using Android Keystore
- **No Cloud Sync**: Keys are stored locally only
- **Secure Transmission**: All API requests use HTTPS
- **Key Rotation**: Support for updating keys without app reinstall

## 🧪 Testing

### **Test API Keys**

1. **Open Settings** → **Manage API Keys**
2. **Click "Test API Keys"**
3. **Check the results**:
   - ✅ **Green**: API key is working
   - ❌ **Red**: API key is invalid or expired
   - ⚠️ **Yellow**: API key has limited functionality

### **Manual Testing**

Test each API individually:

```bash
# Test Finnhub API
curl "https://finnhub.io/api/v1/quote?symbol=AAPL&token=YOUR_FINNHUB_KEY"

# Test TWSE API
curl "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=20250101&stockNo=2330"

# Test ExchangeRate-API
curl "https://v6.exchangerate-api.com/v6/YOUR_EXCHANGE_RATE_KEY/latest/USD"
```

### **Debug Information**

Enable debug logging to troubleshoot API issues:

1. **Open Settings** → **Developer Options**
2. **Enable "Debug Logging"**
3. **Check logs** in Android Studio or via ADB

## 🔍 Troubleshooting

### **Common Issues**

#### **"API Key Invalid" Error**

**Possible Causes**:
- Incorrect API key format
- Expired API key
- API key not activated

**Solutions**:
1. Verify API key format
2. Check API key status on provider website
3. Generate new API key if needed

#### **"Network Error" Message**

**Possible Causes**:
- No internet connection
- API server down
- Firewall blocking requests

**Solutions**:
1. Check internet connection
2. Verify API server status
3. Check firewall settings

#### **"Rate Limit Exceeded" Error**

**Possible Causes**:
- Too many API requests
- Free tier limits exceeded

**Solutions**:
1. Wait for rate limit reset
2. Upgrade to paid plan
3. Reduce API request frequency

### **API Status Check**

Check API status and limits:

1. **Finnhub**: [Status Page](https://finnhub.io/status)
2. **TWSE**: [TWSE Website](https://www.twse.com.tw/)
3. **ExchangeRate-API**: [Status Page](https://exchangerate-api.com/status)

### **Debug Commands**

```bash
# Check API key configuration
adb shell dumpsys activity com.wealthmanager | grep API

# View network requests
adb logcat | grep "API_REQUEST"

# Check encrypted storage
adb shell run-as com.wealthmanager ls -la /data/data/com.wealthmanager/shared_prefs/
```

## 📊 API Usage Guidelines

### **Rate Limits**

| API | Free Tier | Paid Tier | Notes |
|-----|-----------|-----------|-------|
| **Finnhub** | 60 calls/min | 1000+ calls/min | Global stocks |
| **TWSE** | No limit | No limit | Taiwan stocks only |
| **ExchangeRate-API** | 1,500/month | 10,000+ calls/month | Currency rates |

### **Best Practices**

1. **Cache Data**: App caches API responses to reduce calls
2. **Batch Requests**: Multiple requests are batched when possible
3. **Error Handling**: Graceful degradation when APIs are unavailable
4. **Offline Support**: Cached data available when offline

### **Data Privacy**

- **No Personal Data**: API keys are the only data sent to external services
- **Local Storage**: All market data is cached locally
- **Encrypted Keys**: API keys are encrypted using Android Keystore
- **No Tracking**: No user data is collected or shared

## 🔐 Security Considerations

### **API Key Security**

- **Never share API keys** publicly
- **Use environment variables** in development
- **Rotate keys regularly** for security
- **Monitor usage** for unusual activity

### **Network Security**

- **HTTPS Only**: All API requests use TLS 1.3
- **Certificate Pinning**: Validates API server certificates
- **Request Encryption**: Sensitive data encrypted in transit

### **Data Protection**

- **Local Storage**: All data stored locally on device
- **No Cloud Sync**: No data transmitted to external servers
- **Encrypted Database**: Local database encrypted with Android Keystore

## 📞 Support

### **API Provider Support**

- **Finnhub**: [Support](https://finnhub.io/support)
- **TWSE**: [Contact](https://www.twse.com.tw/en/page/contact.html)
- **ExchangeRate-API**: [Support](https://exchangerate-api.com/support)

### **App Support**

- **GitHub Issues**: [Report Issues](https://github.com/kuoyaoming/Wealth-Manager/issues)
- **Documentation**: [README.md](README.md)
- **Security**: [SECURITY.md](SECURITY.md)

### **Common Questions**

**Q: Do I need all three API keys?**
A: No, but having all three provides the best experience. You can use the app with just one API key.

**Q: Are API keys stored securely?**
A: Yes, all API keys are encrypted using Android Keystore and stored locally on your device.

**Q: What happens if I don't have API keys?**
A: The app will work with limited functionality. You won't have real-time market data.

**Q: Can I change API keys later?**
A: Yes, you can update API keys anytime in Settings → Manage API Keys.

---

**Last Updated**: October 2025  
**Next Review**: March 2025

*For more information, see the [Development Guide](DEVELOPMENT.md) and [Security Policy](SECURITY.md).*