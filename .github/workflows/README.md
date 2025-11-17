# 🔄 GitHub Actions Workflows

This directory contains automated workflows for the THISJOWI backend microservices.

## 📋 Available Workflows

### 1. 🏗️ CI - Build & Test (`ci.yml`)

**Triggers:** Push/PR to `master`, `main`, `develop`

**Purpose:** Continuous Integration for all microservices

**What it does:**
- ✅ Builds all 5 microservices in parallel (matrix strategy)
- ✅ Runs unit tests for each service
- ✅ Generates test reports
- ✅ Uploads JAR artifacts
- ✅ Caches Maven dependencies for faster builds

**Services tested:** Authentication, Notes, Password, Cloud Gateway, Eureka

---

### 2. 🐳 Docker Build & Push (`docker-build.yml`)

**Triggers:** Push to `master`/`main`, Tags `v*.*.*`, PRs

**Purpose:** Build and publish Docker images

**What it does:**
- 🐳 Builds Docker images for all services
- 📦 Pushes to GitHub Container Registry (`ghcr.io`)
- 🏷️ Tags images with branch name, SHA, and semantic versions
- 💾 Uses layer caching for faster builds
- 📊 Generates build summary

**Registry:** `ghcr.io/thisjowi/thisjowi-{service}:tag`

---

### 3. 🔒 Security Scanning (`security.yml`)

**Triggers:** Push/PR, Scheduled (Mondays 9 AM UTC)

**Purpose:** Comprehensive security analysis

**What it does:**
- 🔍 **Dependency Scan:** OWASP Dependency Check for vulnerabilities
- 🕵️ **Secret Detection:** TruffleHog scans for leaked secrets
- 🔬 **Code Analysis:** CodeQL security and quality queries
- 🐳 **Container Scan:** Trivy scans Docker images for CVEs
- 📊 Uploads results to GitHub Security tab

**Reports:** Available in Security > Code scanning alerts

---

### 4. 🚀 Release (`release.yml`)

**Triggers:** Git tags matching `v*.*.*` (e.g., `v1.0.0`)

**Purpose:** Automated release process

**What it does:**
- 📝 Generates changelog from PR labels
- 🎉 Creates GitHub release with notes
- 📦 Builds and uploads JAR artifacts
- 🐳 Publishes Docker images with version tags
- 🏷️ Tags images as `latest` and `v1.0.0`

**How to create a release:**
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

### 5. 📊 Code Quality (`code-quality.yml`)

**Triggers:** Push/PR to `master`, `main`, `develop`

**Purpose:** Code quality analysis

**What it does:**
- ✅ **Checkstyle:** Java code style validation
- 📈 **Code Coverage:** JaCoCo reports with Codecov integration
- 🔍 **PMD:** Static analysis for code issues
- 🐛 **SpotBugs:** Bug pattern detection
- 📊 Generates quality summary dashboard

**Tools:** Checkstyle, JaCoCo, PMD, SpotBugs

---

### 6. ☸️ Kubernetes Validation (`kubernetes-validate.yml`)

**Triggers:** Changes to `kubernetes/**` directory

**Purpose:** Validate Kubernetes manifests

**What it does:**
- ✅ Validates YAML syntax with `kubectl`
- 🔍 Schema validation with kubeval & kubeconform
- 🔒 Detects hardcoded secrets in manifests
- ⚠️ Verifies `secret.yaml` is not committed
- 💾 Checks for resource limits in deployments

**Validates:** All `.yaml` files in `kubernetes/` folder

---

## 🎯 Workflow Matrix

| Workflow | On Push | On PR | On Tag | Scheduled | Manual |
|----------|---------|-------|--------|-----------|--------|
| CI Build & Test | ✅ | ✅ | ❌ | ❌ | ✅ |
| Docker Build | ✅ | ✅ | ✅ | ❌ | ✅ |
| Security Scan | ✅ | ✅ | ❌ | ✅ Weekly | ✅ |
| Release | ❌ | ❌ | ✅ | ❌ | ❌ |
| Code Quality | ✅ | ✅ | ❌ | ❌ | ✅ |
| K8s Validation | ✅* | ✅* | ❌ | ❌ | ✅ |

*Only when `kubernetes/**` files change

---

## 🚀 Quick Start

### Running Workflows Manually

1. Go to **Actions** tab in GitHub
2. Select the workflow
3. Click **Run workflow**
4. Choose branch and run

### Creating a Release

```bash
# Tag your commit
git tag -a v1.0.0 -m "Release version 1.0.0"

# Push the tag
git push origin v1.0.0

# The release workflow will automatically:
# - Create GitHub release
# - Build JARs
# - Publish Docker images
```

### Viewing Results

- **CI Results:** Check the green ✅ or red ❌ on commits
- **Test Reports:** Download from workflow artifacts
- **Security Findings:** Security tab > Code scanning
- **Coverage Reports:** Check Codecov badge or artifacts
- **Docker Images:** Packages tab in repository

---

## 📦 Artifacts Generated

Each workflow generates artifacts you can download:

| Workflow | Artifacts |
|----------|-----------|
| CI | JAR files, Test reports |
| Security | Dependency reports, Trivy results |
| Code Quality | Checkstyle, PMD, SpotBugs, Coverage reports |
| Release | Versioned JAR files |

**Retention:** Artifacts kept for 7-30 days

---

## 🔧 Configuration

### Required Secrets

No secrets required! Workflows use `GITHUB_TOKEN` automatically.

### Optional Secrets (for enhanced features)

Add these in **Settings > Secrets and variables > Actions**:

| Secret | Purpose | Required |
|--------|---------|----------|
| `CODECOV_TOKEN` | Upload coverage to Codecov | No |
| `SONAR_TOKEN` | SonarCloud integration | No |
| `DOCKER_HUB_USERNAME` | Push to Docker Hub | No |
| `DOCKER_HUB_TOKEN` | Push to Docker Hub | No |

### Customization

Edit workflow files in `.github/workflows/` to:
- Change trigger branches
- Modify build steps
- Add/remove services
- Adjust schedule times
- Configure notifications

---

## 📊 Status Badges

Add these to your README:

```markdown
![CI](https://github.com/THISJOWI/server/actions/workflows/ci.yml/badge.svg)
![Docker](https://github.com/THISJOWI/server/actions/workflows/docker-build.yml/badge.svg)
![Security](https://github.com/THISJOWI/server/actions/workflows/security.yml/badge.svg)
```

---

## 🐛 Troubleshooting

### Build Failures

**Problem:** Maven build fails

**Solutions:**
1. Check Java version compatibility (Java 21 required)
2. Verify `pom.xml` syntax
3. Clear Maven cache: Delete `~/.m2/repository`
4. Check workflow logs for specific errors

### Docker Push Failures

**Problem:** Cannot push to container registry

**Solutions:**
1. Verify `GITHUB_TOKEN` has package write permissions
2. Check if repository allows package publishing
3. Ensure Docker build completes successfully

### Test Failures

**Problem:** Tests fail in CI but pass locally

**Solutions:**
1. Check environment variables
2. Verify database/redis connections (use mocks in CI)
3. Check timezone differences
4. Review test logs in artifacts

---

## 📚 Learn More

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Security Hardening](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)

---

## 🤝 Contributing

When adding new services:

1. Update the `matrix.service` in relevant workflows
2. Add service-specific build steps if needed
3. Update this README with new services
4. Test workflows in a feature branch first

---

## 📞 Support

Questions about workflows? 

- 📧 Email: support@thisjowi.uk
- 🌐 Website: [thisjowi.uk](https://thisjowi.uk)
- 🐛 Issues: [GitHub Issues](../../issues)

---

**Made with ❤️ by THISJOWI**
