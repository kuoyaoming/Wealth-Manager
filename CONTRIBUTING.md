# Contributing to Wealth Manager

Thank you for your interest in contributing to Wealth Manager! This document provides guidelines and information for contributors.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Contributing Guidelines](#contributing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Code Standards](#code-standards)
- [Testing](#testing)
- [Documentation](#documentation)
- [Release Process](#release-process)

## 🤝 Code of Conduct

This project follows the [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you agree to uphold this code.

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Hedgehog or later
- **JDK**: 17 or later
- **Android SDK**: API 34+ (Android 14+)
- **Git**: Latest version
- **Kotlin**: 1.9.0+

### Development Setup

1. **Fork and Clone**
   ```bash
   # Fork the repository on GitHub
   git clone https://github.com/YOUR_USERNAME/Wealth-Manager.git
   cd Wealth-Manager
   ```

2. **Set up Environment**
   ```bash
   # Copy environment template
   cp local.properties.template local.properties
   
   # Edit local.properties with your configuration
   # See docs/DEVELOPMENT.md for detailed setup
   ```

3. **Build and Test**
   ```bash
   # Build the project
   ./gradlew assembleDebug
   
   # Run tests
   ./gradlew test
   
   # Check code quality
   ./gradlew codeQualityCheck
   ```

## 🛠️ Development Setup

### **IDE Configuration**

#### Android Studio
- Install Android Studio Hedgehog or later
- Install Kotlin plugin
- Configure code style: File → Settings → Editor → Code Style → Kotlin
- Import project and sync Gradle

#### Code Style
- Use the project's `detekt.yml` configuration
- Enable ktlint formatting
- Follow Kotlin coding conventions

### **Environment Variables**

Create `local.properties` with:
```properties
# Android SDK
sdk.dir=/path/to/android/sdk

# API Keys (for development)
FINNHUB_API_KEY=your_finnhub_key
TWSE_API_KEY=your_twse_key
EXCHANGE_RATE_API_KEY=your_exchange_rate_key
```

### **API Keys Setup**

See [API Setup Guide](docs/API_SETUP.md) for detailed instructions on obtaining and configuring API keys.

## 📝 Contributing Guidelines

### **Types of Contributions**

We welcome various types of contributions:

- 🐛 **Bug Fixes**: Fix existing issues
- ✨ **New Features**: Add new functionality
- 📚 **Documentation**: Improve documentation
- 🧪 **Tests**: Add or improve tests
- 🎨 **UI/UX**: Improve user interface
- 🔧 **Refactoring**: Code improvements
- 🌍 **Localization**: Add new languages

### **Before Contributing**

1. **Check Issues**: Look for existing issues or create new ones
2. **Discuss Changes**: For major changes, discuss in issues first
3. **Follow Guidelines**: Read and follow all guidelines
4. **Test Changes**: Ensure all tests pass

### **Contribution Process**

1. **Create Issue**: Describe the problem or feature request
2. **Fork Repository**: Create your fork
3. **Create Branch**: Use descriptive branch names
4. **Make Changes**: Follow coding standards
5. **Add Tests**: Include relevant tests
6. **Update Documentation**: Update docs if needed
7. **Submit PR**: Create pull request with description

## 🔄 Pull Request Process

### **Branch Naming**

Use descriptive branch names:
```bash
# Feature branches
feature/add-biometric-auth
feature/improve-ui-performance

# Bug fix branches
fix/api-key-encryption
fix/memory-leak-dashboard

# Documentation branches
docs/update-readme
docs/add-api-guide
```

### **Commit Messages**

Follow conventional commit format:
```
type(scope): description

feat(auth): add biometric authentication
fix(api): resolve API key encryption issue
docs(readme): update installation instructions
test(dashboard): add unit tests for ViewModel
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### **Pull Request Template**

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Code refactoring

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests pass
- [ ] No breaking changes (or documented)
```

## 📏 Code Standards

### **Kotlin Style Guide**

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `detekt` for static analysis
- Use `ktlint` for code formatting
- Maximum line length: 120 characters

### **Architecture Guidelines**

- **MVVM Pattern**: Use ViewModels for business logic
- **Repository Pattern**: Abstract data sources
- **Dependency Injection**: Use Hilt for DI
- **Reactive Programming**: Use Coroutines and Flow
- **Single Responsibility**: Keep classes focused

### **Code Quality Rules**

```kotlin
// Good: Clear, descriptive names
class BiometricAuthenticationManager

// Bad: Unclear abbreviations
class BioAuthMgr

// Good: Proper error handling
try {
    authenticateUser()
} catch (e: BiometricException) {
    handleBiometricError(e)
}

// Bad: Silent failures
try {
    authenticateUser()
} catch (e: Exception) {
    // Ignore
}
```

### **Documentation Standards**

- **KDoc**: Document all public APIs
- **README**: Update for new features
- **Comments**: Explain complex logic
- **Examples**: Provide usage examples

## 🧪 Testing

### **Test Structure**

```
src/test/java/com/wealthmanager/
├── auth/                    # Authentication tests
├── data/                    # Data layer tests
├── ui/                      # UI tests
└── utils/                   # Utility tests
```

### **Testing Guidelines**

- **Unit Tests**: Test individual components
- **Integration Tests**: Test component interactions
- **UI Tests**: Test user interactions
- **Coverage**: Maintain 80%+ test coverage

### **Running Tests**

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "BiometricAuthManagerTest"

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

### **Test Examples**

```kotlin
@Test
fun `authenticateUser should return success when biometric is available`() {
    // Given
    val mockBiometricManager = mockk<BiometricManager>()
    every { mockBiometricManager.isAvailable() } returns true
    
    // When
    val result = authManager.authenticateUser()
    
    // Then
    assertThat(result).isEqualTo(AuthResult.Success)
}
```

## 📚 Documentation

### **Documentation Types**

- **API Documentation**: KDoc for public APIs
- **Architecture Documentation**: System design docs
- **User Documentation**: User guides and tutorials
- **Developer Documentation**: Setup and development guides

### **Documentation Standards**

- **Markdown**: Use Markdown for documentation
- **Code Examples**: Include working code examples
- **Screenshots**: Add screenshots for UI changes
- **Diagrams**: Use Mermaid for architecture diagrams

### **Updating Documentation**

When making changes:
1. Update relevant documentation
2. Add new sections if needed
3. Update screenshots for UI changes
4. Verify all links work

## 🚀 Release Process

### **Version Numbering**

We follow [Semantic Versioning](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### **Release Checklist**

- [ ] All tests pass
- [ ] Code quality checks pass
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Version numbers updated
- [ ] Release notes prepared

### **Creating a Release**

1. **Update Version**: Update version in `build.gradle`
2. **Update Changelog**: Add entry to `CHANGELOG.md`
3. **Create Tag**: `git tag v1.2.3`
4. **Push Tag**: `git push origin v1.2.3`
5. **Create Release**: Use GitHub release interface

## 🐛 Bug Reports

### **Before Reporting**

1. **Search Issues**: Check if issue already exists
2. **Reproduce**: Ensure you can reproduce the issue
3. **Check Documentation**: Verify it's not documented behavior

### **Bug Report Template**

```markdown
## Bug Description
Clear description of the bug

## Steps to Reproduce
1. Go to '...'
2. Click on '...'
3. See error

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: [e.g. Android 14]
- App Version: [e.g. 1.4.7]
- Device: [e.g. Pixel 8]

## Additional Context
Any other relevant information
```

## ✨ Feature Requests

### **Feature Request Template**

```markdown
## Feature Description
Clear description of the feature

## Use Case
Why is this feature needed?

## Proposed Solution
How should this feature work?

## Alternatives
Any alternative solutions considered?

## Additional Context
Any other relevant information
```

## 📞 Getting Help

### **Communication Channels**

- **GitHub Issues**: For bugs and feature requests
- **GitHub Discussions**: For questions and discussions
- **Pull Request Comments**: For code review discussions

### **Response Time**

- **Issues**: Within 48 hours
- **Pull Requests**: Within 72 hours
- **Questions**: Within 24 hours

## 🏆 Recognition

Contributors will be recognized in:
- **README.md**: Contributor list
- **CHANGELOG.md**: Release notes
- **GitHub**: Contributor statistics

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to Wealth Manager! 🎉

**Questions?** Feel free to open an issue or start a discussion.
