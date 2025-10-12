# Wealth Manager - CI/CD Workflows

This document describes the Continuous Integration and Continuous Deployment (CI/CD) workflows for the Wealth Manager application, managed by GitHub Actions.

##  workflows

There is one primary workflow configured for this project:

- **`release.yml`**: Handles the complete build and release process for the Android application.

---

## Workflow: Build and Release to Google Play (`release.yml`)

This is the main workflow responsible for building, versioning, and deploying the application.

### Trigger

This workflow is automatically triggered when a new **tag** matching the pattern `v*` (e.g., `v1.9.4`, `v2.0.0`) is pushed to the repository.

```yaml
on:
  push:
    tags:
      - 'v*'
