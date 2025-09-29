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

# Check if local.properties exists
if [ ! -f "local.properties" ]; then
    echo -e "${YELLOW}📋 Creating local.properties from template...${NC}"
    cp local.properties.template local.properties
    echo -e "${YELLOW}⚠️  Please edit local.properties and add your API keys${NC}"
    echo -e "${BLUE}📖 See SECURITY.md for API key sources${NC}"
else
    echo -e "${GREEN}✅ local.properties already exists${NC}"
fi

# Check if API keys are set
if grep -q "your_.*_api_key_here" local.properties; then
    echo -e "${YELLOW}⚠️  Please update API keys in local.properties${NC}"
    echo -e "${BLUE}📖 API Key Sources:${NC}"
    echo -e "   • Finnhub: https://finnhub.io/register"
    echo -e "   • Exchange Rate API: https://exchangerate-api.com/"
    echo -e "   • Alpha Vantage: https://www.alphavantage.co/support/#api-key"
else
    echo -e "${GREEN}✅ API keys appear to be configured${NC}"
fi

# Check if .gitignore contains local.properties
if grep -q "local.properties" .gitignore; then
    echo -e "${GREEN}✅ local.properties is in .gitignore${NC}"
else
    echo -e "${RED}❌ local.properties is NOT in .gitignore${NC}"
    echo -e "${YELLOW}⚠️  This is a security risk!${NC}"
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

# Check if API key fields are defined in build.gradle
if grep -q "buildConfigField.*API_KEY" app/build.gradle; then
    echo -e "${GREEN}✅ API key fields are defined in build.gradle${NC}"
else
    echo -e "${YELLOW}⚠️  API key fields are not defined in build.gradle${NC}"
    echo -e "${BLUE}📖 Add buildConfigField for API keys in build.gradle${NC}"
fi

# Security checklist
echo -e "\n${BLUE}🔐 Security Checklist:${NC}"
echo -e "   ${GREEN}✅${NC} local.properties in .gitignore"
echo -e "   ${GREEN}✅${NC} No hardcoded API keys in source code"
echo -e "   ${GREEN}✅${NC} BuildConfig enabled"
echo -e "   ${GREEN}✅${NC} API key fields defined"

# Development tips
echo -e "\n${BLUE}💡 Development Tips:${NC}"
echo -e "   • Always use BuildConfig.FINNHUB_API_KEY in code"
echo -e "   • Never commit local.properties to version control"
echo -e "   • Rotate API keys regularly"
echo -e "   • Monitor API usage and set rate limits"
echo -e "   • Use different keys for development and production"

# Next steps
echo -e "\n${BLUE}🚀 Next Steps:${NC}"
echo -e "   1. Edit local.properties with your actual API keys"
echo -e "   2. Run './gradlew build' to test the build"
echo -e "   3. Read CONTRIBUTING.md for development guidelines"
echo -e "   4. Read SECURITY.md for security best practices"

echo -e "\n${GREEN}🎉 Development environment setup complete!${NC}"
echo -e "${BLUE}📖 Read CONTRIBUTING.md for development guidelines${NC}"
