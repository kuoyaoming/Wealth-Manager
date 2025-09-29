# Development Setup Scripts

This directory contains scripts to help developers set up the development environment.

## 🖥️ Platform Support

| Platform | Script | Description |
|----------|--------|-------------|
| Windows | `setup-dev.bat` | Command Prompt script |
| Windows | `setup-dev.ps1` | PowerShell script (recommended) |
| Linux/Mac | `setup-dev.sh` | Bash script |

## 🚀 Usage

### Windows PowerShell (Recommended)
```powershell
.\docs\setup\setup-dev.ps1
```

### Windows Command Prompt
```cmd
.\docs\setup\setup-dev.bat
```

### Linux/Mac
```bash
./docs/setup/setup-dev.sh
```

## 🔍 What These Scripts Do

- ✅ Check if `local.properties` exists and create from template if needed
- ✅ Verify API keys are configured
- ✅ Check security configuration (`.gitignore`, no hardcoded keys)
- ✅ Validate build configuration
- ✅ Provide development tips and next steps

## 🛠️ Troubleshooting

If you encounter issues:

1. **API Keys Not Loading**: Check `local.properties` format
2. **Build Errors**: Run `.\gradlew clean` and try again
3. **Permission Issues**: Run PowerShell as Administrator

## 📖 Additional Resources

- [Security Policy](../security/SECURITY.md)
- [Contributing Guide](../development/CONTRIBUTING.md)
- [API Setup Guide](../api/API_SETUP.md)
