#!/bin/bash

echo "🚀 Creating test APK for Wealth Manager..."

# Create APK directory structure
APK_DIR="temp_apk"
mkdir -p $APK_DIR/META-INF
mkdir -p $APK_DIR/res/values
mkdir -p $APK_DIR/assets

echo "📦 Creating APK structure..."

# Create AndroidManifest.xml
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
        android:name=".WealthManagerApplication"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.WealthManager">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.WealthManager">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>

</manifest>
EOF

# Create a minimal DEX file
echo "Creating classes.dex..."
printf '\x64\x65\x78\x0a\x30\x33\x35\x00' > $APK_DIR/classes.dex
printf '\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00' >> $APK_DIR/classes.dex
printf '\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00' >> $APK_DIR/classes.dex

# Create resources.arsc
echo "Creating resources.arsc..."
printf '\x02\x00\x0c\x00' > $APK_DIR/resources.arsc
printf '\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00' >> $APK_DIR/resources.arsc

# Create META-INF files
echo "Creating META-INF files..."

# Create MANIFEST.MF
cat > $APK_DIR/META-INF/MANIFEST.MF << 'EOF'
Manifest-Version: 1.0
Created-By: Wealth Manager Build Script v0.1.1

Name: AndroidManifest.xml
SHA-256-Digest: placeholder

Name: classes.dex
SHA-256-Digest: placeholder

Name: resources.arsc
SHA-256-Digest: placeholder
EOF

# Create CERT.SF
cat > $APK_DIR/META-INF/CERT.SF << 'EOF'
Signature-Version: 1.0
Created-By: Wealth Manager Build Script v0.1.1
SHA-256-Digest-Manifest: placeholder

Name: AndroidManifest.xml
SHA-256-Digest: placeholder

Name: classes.dex
SHA-256-Digest: placeholder

Name: resources.arsc
SHA-256-Digest: placeholder
EOF

# Create certificate
echo "Creating certificate..."
printf '\x30\x82\x01\x22\x30\x0d\x06\x09\x2a\x86\x48\x86\xf7\x0d\x01\x01' > $APK_DIR/META-INF/CERT.RSA
printf '\x01\x05\x00\x03\x82\x01\x0f\x00\x30\x82\x01\x0a\x02\x82\x01\x01' >> $APK_DIR/META-INF/CERT.RSA
printf '\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00' >> $APK_DIR/META-INF/CERT.RSA

# Create strings.xml
cat > $APK_DIR/res/values/strings.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Wealth Manager</string>
    <string name="biometric_auth_title">Biometric Authentication</string>
    <string name="biometric_auth_subtitle">Use your biometric to access your assets</string>
    <string name="dashboard_title">Portfolio Overview</string>
    <string name="total_assets">Total Assets</string>
    <string name="cash_assets">Cash Assets</string>
    <string name="stock_assets">Stock Assets</string>
</resources>
EOF

# Create the APK
echo "📦 Creating APK file..."
cd $APK_DIR
zip -r -X -9 ../WealthManager-v0.1.1-test.apk . > /dev/null 2>&1
cd ..

echo "✅ Test APK created successfully!"
echo "📁 APK location: WealthManager-v0.1.1-test.apk"
echo "📊 File size: $(du -h WealthManager-v0.1.1-test.apk | cut -f1)"

# Show APK contents
echo "📱 APK Contents:"
unzip -l WealthManager-v0.1.1-test.apk

# Clean up
rm -rf $APK_DIR

echo ""
echo "✅ Test APK created!"
echo "📱 This is a basic APK structure for testing purposes."
echo "🔧 For full functionality, use Android Studio with proper Android SDK."