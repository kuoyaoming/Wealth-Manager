#!/bin/bash

echo "🚀 Building Wealth Manager APK locally..."

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "❌ ANDROID_HOME not set. Please set Android SDK path:"
    echo "export ANDROID_HOME=/path/to/android-sdk"
    exit 1
fi

# Check if gradlew exists
if [ ! -f "gradlew" ]; then
    echo "❌ gradlew not found. Please run from project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

# Create local.properties if it doesn't exist
if [ ! -f "local.properties" ]; then
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    echo "✅ Created local.properties"
fi

# Clean and build
echo "🧹 Cleaning project..."
./gradlew clean

echo "🔨 Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Copy APK to release directory
    mkdir -p GitHub/release/apk
    cp app/build/outputs/apk/debug/app-debug.apk GitHub/release/apk/WealthManager-v0.1.1-debug.apk
    
    echo "📁 APK copied to: GitHub/release/apk/WealthManager-v0.1.1-debug.apk"
    echo "📊 File size: $(du -h GitHub/release/apk/WealthManager-v0.1.1-debug.apk | cut -f1)"
    
    # Show APK info
    echo "📱 APK Information:"
    aapt dump badging GitHub/release/apk/WealthManager-v0.1.1-debug.apk | grep -E "(package|application-label|application-icon)"
    
else
    echo "❌ Build failed!"
    exit 1
fi