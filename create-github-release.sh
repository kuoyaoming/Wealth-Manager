#!/bin/bash

echo "🚀 Creating GitHub Release for Wealth Manager v0.1.1..."

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo "❌ Error: Not in a git repository"
    exit 1
fi

# Check if APK exists
APK_PATH="GitHub/release/apk/WealthManager-v0.1.1-release.apk"
if [ ! -f "$APK_PATH" ]; then
    echo "❌ Error: APK file not found at $APK_PATH"
    echo "Please run ./build-release-apk.sh first"
    exit 1
fi

# Get repository information
REPO_URL=$(git remote get-url origin 2>/dev/null || echo "")
if [ -z "$REPO_URL" ]; then
    echo "❌ Error: No git remote origin found"
    echo "Please set up a git remote: git remote add origin <repository-url>"
    exit 1
fi

echo "📋 Repository Information:"
echo "   URL: $REPO_URL"
echo "   APK: $APK_PATH"
echo "   Size: $(du -h "$APK_PATH" | cut -f1)"

# Create release notes
RELEASE_NOTES="GitHub/release/apk/RELEASE_NOTES.md"
if [ -f "$RELEASE_NOTES" ]; then
    echo "📝 Using existing release notes from $RELEASE_NOTES"
    cat "$RELEASE_NOTES"
else
    echo "📝 Creating release notes..."
    cat > "release-notes-temp.md" << 'EOF'
# Wealth Manager v0.1.1 Release

## 🎉 New Features
- **Biometric Authentication**: Secure app access with fingerprint/face recognition
- **Material You Theming**: Dynamic color theming based on system settings
- **Multi-language Support**: Auto-detection of Chinese/English system language
- **Local Database**: Room database for secure local data storage
- **Asset Management**: Cash and stock asset tracking
- **Navigation**: Clean navigation between dashboard and assets
- **Responsive UI**: Modern Material 3 design

## 🔧 Technical Details
- **Target SDK**: Android API 34
- **Minimum SDK**: Android API 23
- **Architecture**: MVVM with Jetpack Compose
- **Database**: Room with local encryption
- **Dependency Injection**: Hilt
- **UI Framework**: Jetpack Compose with Material 3

## 📱 Installation
1. Download the APK file
2. Enable "Install from unknown sources" in Android settings
3. Install the APK on your Android device
4. Launch the app and set up biometric authentication

## 🔒 Security Features
- All data stored locally (no cloud sync)
- Biometric protection for app access
- Encrypted local database
- No network permissions except for optional market data

## 🌐 Supported Languages
- English (default)
- Traditional Chinese (繁體中文)

## 📋 Requirements
- Android 6.0 (API 23) or higher
- Device with biometric authentication support
- 50MB free storage space

## 🐛 Known Issues
- Market data integration in progress
- Some UI elements may need refinement
- Biometric authentication requires physical device (not emulator)

## 🔄 Next Steps
- Real-time market data integration
- Enhanced chart visualizations
- Performance optimizations
- Additional language support

---
*Built with ❤️ using Jetpack Compose and Material 3*
EOF
    echo "✅ Release notes created"
fi

echo ""
echo "🎯 GitHub Release Instructions:"
echo "================================"
echo ""
echo "1. Push your changes to GitHub:"
echo "   git push origin <branch-name>"
echo ""
echo "2. Go to your GitHub repository:"
echo "   $REPO_URL"
echo ""
echo "3. Click 'Releases' → 'Create a new release'"
echo ""
echo "4. Fill in the release details:"
echo "   - Tag version: v0.1.1"
echo "   - Release title: Wealth Manager v0.1.1"
echo "   - Description: Copy from release-notes-temp.md (if created)"
echo ""
echo "5. Upload the APK file:"
echo "   - Drag and drop: $APK_PATH"
echo "   - Or click 'Attach binaries' and select the APK"
echo ""
echo "6. Click 'Publish release'"
echo ""
echo "📁 APK File Details:"
echo "   Path: $APK_PATH"
echo "   Size: $(du -h "$APK_PATH" | cut -f1)"
echo "   Created: $(stat -c %y "$APK_PATH")"
echo ""

# Check if we can push to GitHub
if git remote get-url origin | grep -q "github.com"; then
    echo "✅ GitHub repository detected"
    echo "💡 You can also use GitHub CLI if available:"
    echo "   gh release create v0.1.1 '$APK_PATH' --title 'Wealth Manager v0.1.1' --notes-file release-notes-temp.md"
else
    echo "⚠️  Not a GitHub repository - manual upload required"
fi

echo ""
echo "🎉 Ready to create GitHub release!"
echo "📱 APK is ready for distribution: $APK_PATH"