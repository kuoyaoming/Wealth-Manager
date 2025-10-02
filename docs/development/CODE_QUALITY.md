# Code Quality Guidelines

This document describes the code quality tools and practices used in Wealth Manager.

## 🛠️ Code Quality Tools

### Detekt - Static Code Analysis
- **Purpose**: Analyzes Kotlin code for potential bugs, code smells, and style violations
- **Configuration**: `detekt.yml`
- **Baseline**: `detekt-baseline.xml` (tracks existing issues)

### ktlint - Code Formatting
- **Purpose**: Enforces consistent code formatting and style
- **Configuration**: Built-in Android style rules
- **Auto-fix**: Can automatically format code

## 🚀 Usage

### Running Code Quality Checks

```bash
# Run all code quality checks
./gradlew codeQualityCheck

# Format code automatically
./gradlew codeQualityFormat

# Generate comprehensive report
./gradlew codeQualityReport

# Run individual tools
./gradlew detekt
./gradlew ktlintCheck
./gradlew ktlintFormat
```

### IDE Integration

#### Android Studio / IntelliJ IDEA
1. Install ktlint plugin: `Settings > Plugins > ktlint`
2. Enable Detekt plugin: `Settings > Plugins > Detekt`
3. Configure auto-format on save

#### VS Code
1. Install Kotlin Language plugin
2. Install ktlint extension
3. Configure format on save

## 📋 Code Quality Rules

### Detekt Rules (Selected)

#### Complexity
- **CognitiveComplexMethod**: Max 15 complexity
- **LongMethod**: Max 60 lines
- **LongParameterList**: Max 6 parameters
- **TooManyFunctions**: Max 11 functions per class

#### Style
- **MaxLineLength**: 120 characters
- **MagicNumber**: Avoid magic numbers
- **UnusedImports**: Remove unused imports
- **RedundantExplicitType**: Remove redundant types

#### Potential Bugs
- **UnsafeCallOnNullableType**: Safe null handling
- **UnnecessaryNotNullOperator**: Remove unnecessary !!
- **EqualsAlwaysReturnsTrueOrFalse**: Fix equals methods

### ktlint Rules
- Consistent indentation (4 spaces)
- Proper spacing around operators
- Consistent brace placement
- Line length limits
- Import ordering

## 🔧 Configuration

### Detekt Configuration
```yaml
# detekt.yml
build:
  maxIssues: 0
  excludeCorrectable: false

complexity:
  active: true
  LongMethod:
    active: true
    threshold: 60
```

### ktlint Configuration
```gradle
// app/build.gradle
ktlint {
    version = "1.0.1"
    android = true
    enableExperimentalRules = true
}
```

## 📊 Reports

### Detekt Reports
- **Location**: `build/reports/detekt/detekt.html`
- **Format**: HTML, XML, TXT
- **Content**: Code analysis results, complexity metrics

### ktlint Reports
- **Location**: `build/reports/ktlint/ktlint.html`
- **Format**: HTML, XML
- **Content**: Formatting violations, auto-fix suggestions

### Test Coverage Reports
- **Location**: `build/reports/jacoco/testDebugUnitTest/html/index.html`
- **Content**: Line coverage, branch coverage, complexity

## 🚨 Common Issues and Solutions

### Detekt Issues

#### LongMethod
```kotlin
// ❌ Bad: Method too long
fun processUserData(user: User) {
    // 80+ lines of code
}

// ✅ Good: Break into smaller methods
fun processUserData(user: User) {
    validateUser(user)
    saveUser(user)
    notifyUser(user)
}
```

#### MagicNumber
```kotlin
// ❌ Bad: Magic number
if (user.age > 18) { ... }

// ✅ Good: Named constant
private const val ADULT_AGE = 18
if (user.age > ADULT_AGE) { ... }
```

### ktlint Issues

#### Line Length
```kotlin
// ❌ Bad: Line too long
val result = someVeryLongMethodName(parameter1, parameter2, parameter3, parameter4, parameter5)

// ✅ Good: Break into multiple lines
val result = someVeryLongMethodName(
    parameter1,
    parameter2,
    parameter3,
    parameter4,
    parameter5
)
```

#### Spacing
```kotlin
// ❌ Bad: Inconsistent spacing
val x=1+2
if(x>0){...}

// ✅ Good: Consistent spacing
val x = 1 + 2
if (x > 0) { ... }
```

## 🔄 CI Integration

Code quality checks are automatically run in CI:

1. **Pull Requests**: All checks must pass
2. **Main Branch**: Quality gates enforced
3. **Release**: Quality checks before release

### CI Quality Gates
- Detekt: 0 issues (new violations)
- ktlint: 0 formatting violations
- Test Coverage: ≥80%
- All tests passing

## 📈 Best Practices

### Before Committing
1. Run `./gradlew codeQualityFormat`
2. Run `./gradlew codeQualityCheck`
3. Fix any remaining issues
4. Commit clean code

### Code Review
1. Check Detekt reports
2. Verify formatting consistency
3. Review test coverage
4. Ensure no new violations

### Continuous Improvement
1. Update baseline as issues are fixed
2. Gradually increase quality standards
3. Monitor quality metrics over time
4. Share knowledge with team

## 🆘 Troubleshooting

### Common Problems

#### Detekt Baseline Issues
```bash
# Update baseline after fixing issues
./gradlew detektBaseline
```

#### ktlint Formatting Issues
```bash
# Auto-fix formatting issues
./gradlew ktlintFormat
```

#### Test Coverage Issues
```bash
# Generate coverage report
./gradlew jacocoTestReport
```

### Getting Help
- Check tool documentation
- Review configuration files
- Ask team members
- Create issue if needed

---

**Remember**: Code quality is an ongoing process. Start with the baseline and gradually improve over time.
