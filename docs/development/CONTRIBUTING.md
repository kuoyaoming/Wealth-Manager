# Contributing to Wealth Manager

Thank you for your interest in contributing to Wealth Manager! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Contributing Guidelines](#contributing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Reporting](#issue-reporting)
- [Development Workflow](#development-workflow)

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you agree to uphold this code.

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 36 (Android 16)
- Kotlin 1.9.0+
- JDK 17+
- Git

### Development Setup

1. **Fork the repository**
   ```bash
   # Fork on GitHub, then clone your fork
   git clone https://github.com/YOUR_USERNAME/Wealth-Manager.git
   cd Wealth-Manager
   ```

2. **Set up the development environment**
   ```bash
   # Add upstream remote
   git remote add upstream https://github.com/kuoyaoming/Wealth-Manager.git
   
   # Create a development branch
   git checkout -b development
   
   # Run setup script
   # Linux/Mac
   ./setup-dev.sh
   
   # Windows (Command Prompt)
   setup-dev.bat
   
   # Windows (PowerShell)
   .\setup-dev.ps1
   ```

3. **Open in Android Studio**
   - Open the project in Android Studio
   - Sync Gradle files
   - Run the app on an emulator or device

## Contributing Guidelines

### Types of Contributions

We welcome various types of contributions:

- 🐛 **Bug fixes**
- ✨ **New features**
- 📚 **Documentation improvements**
- 🎨 **UI/UX enhancements**
- ⚡ **Performance optimizations**
- 🧪 **Test coverage**
- 🌍 **Internationalization**

### Before You Start

1. **Check existing issues** - Look for open issues that match your contribution
2. **Create an issue** - For new features, create an issue first to discuss
3. **Assign yourself** - Comment on the issue to let others know you're working on it

### Development Standards

#### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused
- Follow MVVM architecture pattern

#### Commit Messages

Use clear, descriptive commit messages:

```
feat: add biometric authentication timeout
fix: resolve memory leak in MarketDataService
docs: update API documentation
style: format code according to conventions
refactor: extract common UI components
test: add unit tests for AssetRepository
```

#### Code Quality

- Write unit tests for new features
- Ensure all tests pass
- Follow existing code patterns
- Use dependency injection (Hilt)
- Handle errors gracefully

## Pull Request Process

### Before Submitting

1. **Update your fork**
   ```bash
   git fetch upstream
   git checkout main
   git merge upstream/main
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/issue-number
   ```

3. **Make your changes**
   - Write clean, well-documented code
   - Add tests for new functionality
   - Update documentation if needed

4. **Test your changes**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

5. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

### Pull Request Template

When creating a pull request, please include:

- **Description**: What changes were made and why
- **Type**: Bug fix, feature, documentation, etc.
- **Testing**: How the changes were tested
- **Screenshots**: For UI changes
- **Breaking Changes**: Any breaking changes and migration steps

### Review Process

1. **Automated checks** must pass
2. **Code review** by maintainers
3. **Testing** on different devices/Android versions
4. **Documentation** review if applicable

## Issue Reporting

### Bug Reports

When reporting bugs, please include:

- **Environment**: Android version, device model
- **Steps to reproduce**: Clear, numbered steps
- **Expected behavior**: What should happen
- **Actual behavior**: What actually happens
- **Screenshots**: If applicable
- **Logs**: Relevant logcat output

### Feature Requests

For new features, please include:

- **Use case**: Why is this feature needed?
- **Proposed solution**: How should it work?
- **Alternatives**: Other approaches considered
- **Additional context**: Any other relevant information

## Development Workflow

### Branch Naming

- `feature/description` - New features
- `fix/description` - Bug fixes
- `docs/description` - Documentation updates
- `refactor/description` - Code refactoring
- `test/description` - Test improvements

### Testing

#### Unit Tests
```bash
./gradlew test
```

#### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

#### Code Coverage
```bash
./gradlew jacocoTestReport
```

### Architecture Guidelines

#### MVVM Pattern
- **Model**: Data entities and business logic
- **View**: Compose UI components
- **ViewModel**: UI state management and business logic coordination

#### Dependency Injection
- Use Hilt for dependency injection
- Provide dependencies through modules
- Keep dependencies minimal and focused

#### Database
- Use Room for local data storage
- Follow repository pattern
- Implement proper data validation

## Project Structure

```
app/src/main/java/com/wealthmanager/
├── auth/                 # Authentication modules
├── data/                 # Data layer (API, Database, Repository)
│   ├── api/             # API interfaces
│   ├── dao/              # Database access objects
│   ├── entity/           # Data entities
│   ├── repository/       # Repository implementations
│   └── service/          # Business logic services
├── di/                   # Dependency injection modules
├── ui/                   # UI layer
│   ├── assets/           # Asset management screens
│   ├── auth/             # Authentication screens
│   ├── dashboard/        # Dashboard screens
│   ├── components/       # Reusable UI components
│   └── theme/            # Theme and styling
└── utils/                # Utility classes
```

## Getting Help

- **Discussions**: Use GitHub Discussions for questions
- **Issues**: Create issues for bugs and feature requests
- **Documentation**: Check existing documentation first

## Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- Project documentation

Thank you for contributing to Wealth Manager! 🚀
