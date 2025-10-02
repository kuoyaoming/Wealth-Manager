# Security Policy

## 🔒 Security Overview

Wealth Manager is designed with security and privacy as core principles. This document outlines our security practices, vulnerability reporting process, and security features.

## 🛡️ Security Features

### **Data Protection**
- **Local-Only Storage**: All financial data is stored locally on the device
- **Encrypted Database**: Room database with Android Keystore encryption
- **No Cloud Sync**: Complete privacy with no data transmission to external servers
- **Secure Deletion**: Complete data removal when uninstalling

### **Authentication Security**
- **Biometric Authentication**: Hardware-backed fingerprint/face recognition
- **Session Management**: 24-hour automatic session timeout
- **Secure Storage**: API keys encrypted using Android Keystore
- **No Password Storage**: No passwords stored locally or remotely

### **Network Security**
- **HTTPS Only**: All API communications use TLS 1.3
- **Certificate Pinning**: Validates API server certificates
- **Request Encryption**: Sensitive data encrypted in transit
- **API Key Rotation**: Support for key rotation and management

### **Code Security**
- **Static Analysis**: Detekt and ktlint for code quality
- **Dependency Scanning**: Regular security updates for dependencies
- **ProGuard/R8**: Code obfuscation in release builds
- **Secure Coding**: Following Android security best practices

## 🚨 Supported Versions

We provide security updates for the following versions:

| Version | Supported          | Security Updates |
| ------- | ------------------ | ---------------- |
| 1.4.x   | ✅ Yes             | ✅ Yes           |
| 1.3.x   | ❌ No              | ❌ No            |
| 1.2.x   | ❌ No              | ❌ No            |
| 1.1.x   | ❌ No              | ❌ No            |
| 1.0.x   | ❌ No              | ❌ No            |
| < 1.0   | ❌ No              | ❌ No            |

## 🔍 Reporting a Vulnerability

### **Security Vulnerability Reporting**

We take security vulnerabilities seriously. If you discover a security vulnerability, please report it responsibly.

### **How to Report**

1. **DO NOT** create a public GitHub issue
2. **DO NOT** discuss the vulnerability publicly
3. **DO** report privately using one of these methods:

#### **Preferred Method: GitHub Security Advisories**
1. Go to the [Security tab](https://github.com/kuoyaoming/Wealth-Manager/security) in our repository
2. Click "Report a vulnerability"
3. Fill out the security advisory form

#### **Alternative Method: Email**
Send an email to: `security@wealthmanager.app`

### **What to Include**

Please include the following information:

- **Description**: Clear description of the vulnerability
- **Steps to Reproduce**: Detailed steps to reproduce the issue
- **Impact**: Potential impact of the vulnerability
- **Environment**: OS version, app version, device model
- **Proof of Concept**: If applicable, include a minimal reproduction case
- **Suggested Fix**: If you have ideas for fixing the issue

### **Response Timeline**

- **Initial Response**: Within 24 hours
- **Status Update**: Within 72 hours
- **Resolution**: Within 30 days (depending on severity)

### **What Happens Next**

1. **Acknowledgment**: We'll acknowledge receipt within 24 hours
2. **Investigation**: We'll investigate the vulnerability
3. **Status Updates**: Regular updates on progress
4. **Fix Development**: We'll develop and test a fix
5. **Release**: We'll release a security update
6. **Disclosure**: Coordinated disclosure after fix is available

## 🔐 Security Best Practices

### **For Users**

- **Keep App Updated**: Always use the latest version
- **Secure Device**: Use device lock screen and biometric authentication
- **API Keys**: Keep your API keys secure and don't share them
- **Regular Backups**: Backup your data regularly (though it's stored locally)
- **Device Security**: Keep your device OS updated

### **For Developers**

- **Secure Coding**: Follow Android security guidelines
- **Dependency Updates**: Keep dependencies updated
- **Code Review**: Thorough security review of all changes
- **Testing**: Security testing for all features
- **Documentation**: Document security considerations

## 🛠️ Security Architecture

### **Data Flow Security**

```
User Input → Validation → Encryption → Local Storage
     ↓
API Request → TLS 1.3 → Certificate Validation → API Server
     ↓
Response → Decryption → Validation → UI Display
```

### **Authentication Flow**

```
App Launch → Biometric Check → Session Creation → App Access
     ↓
Session Timeout → Re-authentication Required
```

### **API Security**

```
API Key → Android Keystore → Encrypted Storage
     ↓
Request → TLS 1.3 → Certificate Pinning → API Server
```

## 🔍 Security Testing

### **Automated Security Testing**

- **Static Analysis**: Detekt and ktlint
- **Dependency Scanning**: Automated vulnerability scanning
- **Code Quality**: Automated code quality checks
- **Build Security**: Secure build process

### **Manual Security Testing**

- **Penetration Testing**: Regular security assessments
- **Code Review**: Security-focused code reviews
- **Threat Modeling**: Regular threat model updates
- **Security Audits**: Third-party security audits

## 📋 Security Checklist

### **For Contributors**

- [ ] No hardcoded secrets or API keys
- [ ] Input validation for all user inputs
- [ ] Secure error handling (no sensitive data in logs)
- [ ] Proper authentication checks
- [ ] Secure data storage practices
- [ ] HTTPS for all network requests
- [ ] No sensitive data in URLs or logs

### **For Releases**

- [ ] Security review completed
- [ ] Vulnerability scan passed
- [ ] Dependencies updated
- [ ] Code obfuscation enabled
- [ ] Security testing completed
- [ ] Security documentation updated

## 🚫 Security Boundaries

### **What We Don't Do**

- **No Data Collection**: We don't collect any personal data
- **No Analytics**: No tracking or analytics collection
- **No Third-Party Sharing**: No data sharing with third parties
- **No Cloud Storage**: No data stored in the cloud
- **No Remote Logging**: No remote logging of sensitive data

### **What We Do**

- **Local Storage Only**: All data stored locally
- **Encrypted Storage**: All data encrypted at rest
- **Secure Communication**: All API communication encrypted
- **Privacy First**: Privacy by design principles
- **Transparent Security**: Open about security practices

## 📚 Security Resources

### **Android Security**

- [Android Security Guidelines](https://developer.android.com/topic/security)
- [Android Keystore System](https://developer.android.com/training/articles/keystore)
- [Biometric Authentication](https://developer.android.com/training/sign-in/biometric-auth)

### **Cryptography**

- [Android Cryptography](https://developer.android.com/guide/topics/security/cryptography)
- [TLS Best Practices](https://developer.android.com/training/articles/security-ssl)
- [Certificate Pinning](https://developer.android.com/training/articles/security-ssl#CertificatePinning)

### **Privacy**

- [Android Privacy](https://developer.android.com/training/articles/user-data)
- [Data Protection](https://developer.android.com/training/articles/user-data#ProtectUserData)
- [Local Storage](https://developer.android.com/training/articles/user-data#LocalStorage)

## 🆘 Security Incidents

### **If You Suspect a Security Incident**

1. **Stop Using the App**: Immediately stop using the app
2. **Report the Issue**: Follow the vulnerability reporting process
3. **Secure Your Device**: Ensure your device is secure
4. **Change API Keys**: If applicable, rotate your API keys
5. **Monitor for Updates**: Watch for security updates

### **Our Response to Security Incidents**

1. **Immediate Assessment**: Assess the severity and impact
2. **User Notification**: Notify affected users if necessary
3. **Fix Development**: Develop and test a fix
4. **Security Update**: Release a security update
5. **Post-Incident Review**: Learn from the incident

## 📞 Contact Information

### **Security Team**

- **GitHub Security**: [Security Advisories](https://github.com/kuoyaoming/Wealth-Manager/security)
- **Email**: `security@wealthmanager.app`
- **Response Time**: Within 24 hours

### **General Support**

- **GitHub Issues**: [Issues](https://github.com/kuoyaoming/Wealth-Manager/issues)
- **Documentation**: [Security Docs](docs/SECURITY.md)

## 📄 Legal

### **Security Disclosure**

- **Coordinated Disclosure**: We follow coordinated disclosure practices
- **Responsible Disclosure**: We appreciate responsible disclosure
- **No Legal Action**: We won't take legal action against security researchers
- **Recognition**: We recognize security researchers who help improve security

### **Privacy Policy**

- **Data Collection**: We don't collect personal data
- **Data Storage**: All data stored locally on your device
- **Data Sharing**: No data sharing with third parties
- **Data Deletion**: Complete data deletion when uninstalling

---

**Last Updated**: October 2025  
**Next Review**: March 2025

**Questions?** Contact us at `security@wealthmanager.app`
