# Security Policy

## 🔐 API Key Management

### For Developers

1. **Never commit API keys to version control**
2. **Use local.properties for local development**
3. **Use environment variables for CI/CD**

### Setting up API Keys (Developers)

1. Copy `local.properties.template` to `local.properties`
2. Add your actual API keys to `local.properties`
3. Ensure `local.properties` is in `.gitignore`

### API Key Sources

- **Finnhub**: https://finnhub.io/register
- **Exchange Rate API**: https://exchangerate-api.com/
- **Alpha Vantage**: https://www.alphavantage.co/support/#api-key
- **User-provided (in-app)**: Settings → Manage API Keys → Validate & Save

### Security Best Practices

- Rotate API keys regularly
- Use different keys for development and production
- Monitor API usage and set rate limits
- Never share API keys in public repositories

## 🛡️ Application Security

### Data Protection

- **Local-only storage**: All data is encrypted and stored locally on device
- **No cloud sync**: Complete privacy protection
- **Biometric authentication**: No passwords required, 24-hour session timeout
- **Session management**: Automatic authentication state management

### Network Security

- **HTTPS only**: All API communications use secure HTTPS
- **Certificate pinning**: Enhanced security for API communications
- **Request validation**: All API requests are validated before sending
- **Error handling**: Secure error handling without exposing sensitive information

### API Keys at Runtime

- **Encrypted storage**: User API keys stored with EncryptedSharedPreferences (AES-256)
- **Redacted logs**: Keys never logged; UI shows masked preview only (first 6 chars)
- **Backup exclusions**: Keys excluded from cloud backup and device transfer
- **Fallback**: User key takes precedence; falls back to BuildConfig when absent

## 🔒 Privacy Policy

### Data Collection

- **No personal data collection**: App does not collect personal information
- **Local data only**: All financial data stored locally on device
- **No analytics tracking**: No user behavior tracking
- **No third-party sharing**: No data shared with third parties

### Data Storage

- **Encrypted storage**: All data encrypted using Android's built-in encryption
- **No cloud backup**: Data never leaves the device
- **Secure deletion**: Data can be completely removed from device
- **No data retention**: No data retained after app uninstall

## 🚨 Security Vulnerabilities

### Reporting Security Issues

If you discover a security vulnerability, please report it responsibly:

1. **DO NOT** create a public GitHub issue
2. **DO NOT** discuss the vulnerability publicly
3. **Email**: [security@wealthmanager.app](mailto:security@wealthmanager.app)
4. **Include**: Detailed description and steps to reproduce

### Response Timeline

- **Initial response**: Within 24 hours
- **Status update**: Within 72 hours
- **Resolution**: Within 30 days (depending on severity)

### Security Measures

- **Regular security audits**: Quarterly security reviews
- **Dependency updates**: Regular dependency vulnerability scanning
- **Code reviews**: All code changes reviewed for security issues
- **Penetration testing**: Annual security testing

## 🔧 Security Configuration

### Development Environment

```bash
# Copy template file
cp local.properties.template local.properties

# Add your API keys to local.properties
# Never commit local.properties to version control
```

### Production Environment

- Use environment variables for API keys
- Enable ProGuard/R8 obfuscation
- Use signed APKs only
- Regular security updates

### API Key Management

- Build-time keys removed. Keys are provided by users in-app.
- Stored with EncryptedSharedPreferences; redacted in logs; excluded from backups.

## 📋 Security Checklist

### For Contributors

- [ ] No hardcoded API keys in source code
- [ ] All sensitive data in local.properties
- [ ] local.properties in .gitignore
- [ ] Use HTTPS for all network requests
- [ ] Validate all user inputs
- [ ] Handle errors securely
- [ ] Use encrypted storage for sensitive data

### For Users

- [ ] Enable biometric authentication
- [ ] Keep app updated
- [ ] Use secure device lock screen
- [ ] Regular device security updates
- [ ] Avoid public Wi-Fi for sensitive operations

## 🆘 Security Incident Response

### If API Key is Compromised

1. **Immediately revoke** the compromised API key
2. **Generate new API key** from provider
3. **Update local.properties** with new key
4. **Monitor usage** for suspicious activity
5. **Report incident** if necessary

### If Data Breach Occurs

1. **Assess impact** of the breach
2. **Notify users** if personal data affected
3. **Implement fixes** to prevent recurrence
4. **Update security measures** as needed
5. **Document lessons learned**

## 📞 Contact

For security-related questions or concerns:

- **Email**: [security@wealthmanager.app](mailto:security@wealthmanager.app)
- **GitHub**: Create a private security advisory
- **Response time**: Within 24 hours

---

**Last Updated**: 2025  
**Version**: 1.2.1  
**Review Schedule**: Quarterly
