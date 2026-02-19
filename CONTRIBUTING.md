# Contributing to Google Analytics Plugin for dotCMS

Thank you for your interest in contributing! We welcome pull requests that improve the plugin.

## Pull Requests

#### Before You Start

1. **Keep changes focused** - One feature/fix per PR
2. **Fork the repository** - Work on your own fork

#### Development Setup

1. **Fork and clone the repository**
   ```bash
   git fork https://github.com/dotCMS/google-analytics.git
   cd google-analytics
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/issue-description
   ```

3. **Set up your development environment**
   - JDK 11 or higher
   - Gradle (included via wrapper)
   - A running dotCMS instance for testing (local or Docker)

4. **Build the plugin**
   ```bash
   ./gradlew clean jar
   ```

   The JAR will be in `build/libs/google-analytics-X.X.X.jar`

#### Making Changes

1. **Write clean, readable code**
   - Follow existing code style and patterns
   - Add comments for complex logic
   - Keep methods focused and concise

2. **Test your changes**
   - Build the plugin: `./gradlew jar`
   - Upload to a dotCMS instance
   - Test with real Google Analytics data
   - Verify OSGi bundle loads without errors
   - Test Velocity viewtool functionality

3. **Update documentation**
   - Update README.md if you changed functionality
   - Add/update code comments
   - Document new viewtool methods or parameters

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "Brief description of changes

   Longer explanation of what changed and why.
   Include any breaking changes or migration notes."
   ```

   **Commit message guidelines:**
   - Use present tense ("Add feature" not "Added feature")
   - Be concise but descriptive
   - Reference issues when applicable (`Fixes #123`)

#### Submitting Your PR

1. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create a Pull Request**
   - Go to the [repository](https://github.com/dotCMS/google-analytics)
   - Click "New Pull Request"
   - Select your fork and branch
   - Fill out the PR template with:
     - **What changed** - Clear description of changes
     - **Why** - The problem this solves
     - **Testing** - How you tested the changes
     - **Breaking changes** - Any compatibility issues
     - **Related issues** - Link to related issues

3. **Address review feedback**
   - Be responsive to comments
   - Make requested changes in new commits
   - Ask questions if feedback is unclear

## Development Guidelines

### Code Style

- **Java**: Follow standard Java conventions
- **Indentation**: 4 spaces (no tabs)
- **Braces**: Opening brace on same line
- **Naming**:
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`

### OSGi Considerations

When adding dependencies:

1. **Check if dotCMS already provides it** - Use `compileOnly` if yes
2. **Bundle third-party libraries** - Add to `osgiLibs` configuration
3. **Update Import-Package** - Exclude bundled packages from imports
4. **Test OSGi wiring** - Verify bundle loads in clean dotCMS instance

Example from `build.gradle`:
```gradle
dependencies {
    compileOnly('com.dotcms:dotcms:23.01.10') { transitive = true }
    implementation (group: 'your.library', name: 'artifact', version: '1.0.0')
    osgiLibs (group: 'your.library', name: 'artifact', version: '1.0.0')
}

'Import-Package': '''
    !your.library.*,
    javax.*,
    com.dotcms.*,
    ...
'''
```

### Testing Checklist

Before submitting a PR, verify:

- [ ] Plugin builds without errors: `./gradlew clean jar`
- [ ] JAR uploads successfully to dotCMS
- [ ] OSGi bundle starts without errors (check logs for "Starting Google Analytics OSGI plugin")
- [ ] Viewtool is available in Velocity (`$googleanalytics`)
- [ ] Can create analytics request and query GA4 data
- [ ] No breaking changes to existing Velocity code (or documented if necessary)
- [ ] Works with dotCMS 23.01.10 and newer

## Versioning

This plugin follows [Semantic Versioning](https://semver.org/):

- **Major (X.0.0)**: Breaking changes
- **Minor (0.X.0)**: New features, backward compatible
- **Patch (0.0.X)**: Bug fixes, backward compatible

**IMPORTANT:** Bump the version in `build.gradle` for each PR that should trigger a new release:

```gradle
version = '0.4.2'  // Increment for your changes
```

When merged to main, GitHub Actions will:
1. Check if release `v0.4.2` exists
2. If not, build the JAR and create the release
3. If yes, skip release creation (no duplicates)

**Always bump the version** to ensure your changes are trackable in releases.

## Release Process

Releases are automated via GitHub Actions:

1. PR is merged to `main`
2. GitHub Actions builds the plugin
3. Creates a GitHub release with the JAR attached
4. Tags the release with version from `build.gradle`

Only maintainers can merge to `main` and trigger releases.

## Questions?

- **General questions**: [dotCMS Community Forums](https://dotcms.com/forums)
- **dotCMS development**: [dotCMS Developer Docs](https://dotcms.com/docs)

## License

By contributing, you agree that your contributions will be licensed under the same terms as the project.

---

Thank you for contributing to make dotCMS better! 🎉
