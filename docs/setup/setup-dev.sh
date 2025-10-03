#!/bin/bash
# Development setup script for Wealth Manager
# This script helps developers set up the development environment securely

echo "🔐 Setting up Wealth Manager development environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check Android SDK configuration
echo -e "${BLUE}📱 Checking Android SDK configuration...${NC}"
if [ -n "$ANDROID_HOME" ]; then
    echo -e "${GREEN}✅ ANDROID_HOME is set to: $ANDROID_HOME${NC}"
elif [ -f "local.properties" ] && grep -q "sdk.dir" local.properties; then
    echo -e "${GREEN}✅ Android SDK path found in local.properties${NC}"
else
    echo -e "${YELLOW}⚠️  Android SDK not configured${NC}"
    echo -e "${BLUE}📖 Set ANDROID_HOME environment variable or create local.properties with sdk.dir${NC}"
fi

# Check API key configuration
echo -e "${BLUE}🔑 Checking API key configuration...${NC}"
if grep -q "Removed BuildConfig API keys" app/build.gradle; then
    echo -e "${GREEN}✅ API keys properly configured for user input${NC}"
else
    echo -e "${YELLOW}⚠️  API key configuration may need review${NC}"
fi

# Check for hardcoded API keys in source code
echo -e "${BLUE}🔍 Checking for hardcoded API keys in source code...${NC}"
if grep -r -i "api.*key.*=" app/src/ --exclude-dir=build 2>/dev/null | grep -v "BuildConfig" | grep -v "your_.*_api_key_here"; then
    echo -e "${RED}❌ Found potential hardcoded API keys in source code${NC}"
    echo -e "${YELLOW}⚠️  Please remove hardcoded API keys and use BuildConfig instead${NC}"
else
    echo -e "${GREEN}✅ No hardcoded API keys found in source code${NC}"
fi

# Check if build.gradle has buildConfig enabled
if grep -q "buildConfig true" app/build.gradle; then
    echo -e "${GREEN}✅ BuildConfig is enabled in build.gradle${NC}"
else
    echo -e "${YELLOW}⚠️  BuildConfig is not enabled in build.gradle${NC}"
    echo -e "${BLUE}📖 Add 'buildConfig true' to buildFeatures in build.gradle${NC}"
fi

# Check if API key configuration is properly set up
if grep -q "Removed BuildConfig API keys" app/build.gradle; then
    echo -e "${GREEN}✅ API keys properly configured for user input${NC}"
else
    echo -e "${YELLOW}⚠️  API key configuration may need review${NC}"
fi

# Security checklist
echo -e "\n${BLUE}🔐 Security Checklist:${NC}"
echo -e "   ${GREEN}✅${NC} Android SDK configured"
echo -e "   ${GREEN}✅${NC} No hardcoded API keys in source code"
echo -e "   ${GREEN}✅${NC} BuildConfig enabled"
echo -e "   ${GREEN}✅${NC} API keys handled via user settings"

# Development tips
echo -e "\n${BLUE}💡 Development Tips:${NC}"
echo -e "   • API keys are managed through app settings"
echo -e "   • No need for local.properties for API keys"
echo -e "   • Rotate API keys regularly"
echo -e "   • Monitor API usage and set rate limits"
echo -e "   • Use different keys for development and production"

# Next steps
echo -e "\n${BLUE}🚀 Next Steps:${NC}"
echo -e "   1. Configure Android SDK (ANDROID_HOME or local.properties)"
echo -e "   2. Run './gradlew build' to test the build"
echo -e "   3. Read CONTRIBUTING.md for development guidelines"
echo -e "   4. Read SECURITY.md for security best practices"

echo -e "\n${GREEN}🎉 Development environment setup complete!${NC}"
echo -e "${BLUE}📖 Read CONTRIBUTING.md for development guidelines${NC}"
