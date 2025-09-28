#!/bin/bash

echo "🚀 Building minimal Wealth Manager APK..."

# Create output directory
mkdir -p GitHub/release/apk

# Create a minimal APK structure
echo "📦 Creating minimal APK structure..."

# Create APK directory
APK_DIR="temp_apk"
mkdir -p $APK_DIR/META-INF
mkdir -p $APK_DIR/res
mkdir -p $APK_DIR/assets

# Create minimal AndroidManifest.xml
cat > $APK_DIR/AndroidManifest.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.wealthmanager"
    android:versionCode="1"
    android:versionName="0.1.1">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    <uses-permission android:name="android.permission.USE_FINGERPRINT" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Wealth Manager"
        android:theme="@android:style/Theme.Material.Light">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="Wealth Manager">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>

</manifest>
EOF

# Create minimal classes.dex (placeholder)
echo "Creating minimal classes.dex..."
# This would normally be created by dx tool from Android SDK
touch $APK_DIR/classes.dex

# Create META-INF files
echo "Creating META-INF files..."

# Create MANIFEST.MF
cat > $APK_DIR/META-INF/MANIFEST.MF << 'EOF'
Manifest-Version: 1.0
Created-By: Wealth Manager Build Script
EOF

# Create CERT.SF
cat > $APK_DIR/META-INF/CERT.SF << 'EOF'
Signature-Version: 1.0
Created-By: Wealth Manager Build Script
EOF

# Create CERT.RSA (placeholder)
echo "Creating certificate files..."
# This would normally be a real certificate
touch $APK_DIR/META-INF/CERT.RSA

# Create a simple APK using zip
echo "📦 Creating APK file..."
cd $APK_DIR
zip -r ../WealthManager-v0.1.1-minimal.apk . > /dev/null 2>&1
cd ..

# Move APK to release directory
mv WealthManager-v0.1.1-minimal.apk GitHub/release/apk/

echo "✅ Minimal APK created successfully!"
echo "📁 APK location: GitHub/release/apk/WealthManager-v0.1.1-minimal.apk"
echo "📊 File size: $(du -h GitHub/release/apk/WealthManager-v0.1.1-minimal.apk | cut -f1)"

# Show APK contents
echo "📱 APK Contents:"
unzip -l GitHub/release/apk/WealthManager-v0.1.1-minimal.apk

# Clean up
rm -rf $APK_DIR

echo ""
echo "⚠️  NOTE: This is a minimal APK for testing purposes only."
echo "   For a fully functional APK, use Android Studio or proper Android SDK."
echo "   See BUILD_INSTRUCTIONS.md for complete build process."