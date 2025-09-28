#!/bin/bash

echo "🚀 Building Wealth Manager APK using Docker..."

# Create output directory
mkdir -p apk

# Build using Docker
echo "📦 Building APK using Docker..."
docker build -t wealth-manager-builder .

if [ $? -eq 0 ]; then
    echo "✅ Docker build successful"
    
    # Extract APK from container
    echo "📤 Extracting APK..."
    docker run --name temp-container wealth-manager-builder
    docker cp temp-container:/output/WealthManager-v0.1.1-debug.apk apk/
    docker rm temp-container
    
    echo "✅ APK extracted successfully"
    echo "📁 APK location: apk/WealthManager-v0.1.1-debug.apk"
    
    # Show file info
    ls -la apk/
    
else
    echo "❌ Docker build failed"
    exit 1
fi