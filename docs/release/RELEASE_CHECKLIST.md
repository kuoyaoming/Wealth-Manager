# Release Checklist (v1.4.0)

## Pre-release
- [ ] Confirm all CI checks are green on main branch
- [ ] Update `CHANGELOG.md` with 1.4.0 notes
- [ ] Verify `README.md` and `README_zh.md` versioning sections
- [ ] Ensure no secrets or API keys are in source
- [ ] Confirm Play Console service account and signing configs are valid

## Tag & Build
- [ ] Create release tag: `git tag v1.4.0`
- [ ] Push tag: `git push origin v1.4.0`
- [ ] Wait for CI to finish release job

## Artifacts
- [ ] Download `.aab` from CI artifacts
- [ ] Download `mapping.txt` for release
- [ ] Archive artifacts in internal storage

## Play Console
- [ ] Upload AAB to internal/closed testing track
- [ ] Fill release notes (paste from `CHANGELOG.md` 1.4.0)
- [ ] Submit for review

## Post-release
- [ ] Create GitHub Release with tag `v1.4.0` and changelog
- [ ] Monitor crash reports and user feedback
- [ ] Plan minor fixes (1.4.1) if needed

> Note: versionName is derived from tag; versionCode is from CI `GITHUB_RUN_NUMBER`. Release bundles are only produced on CI.
