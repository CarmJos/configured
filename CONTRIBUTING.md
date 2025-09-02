# Contributing Guide

> English is the primary language. A brief Chinese hint: 若需中文协助可在 Issue 中说明。

Thank you for investing time in contributing! This document describes how to propose changes and how we maintain quality and consistency across the project.

## Quick Links
- Code of Conduct: ./CODE_OF_CONDUCT.md
- Security Policy: ./SECURITY.md
- Issues: https://github.com/CarmJos/configured/issues
- Discussions / Q&A: (open an Issue if Discussions are disabled)

## Table of Contents
1. Principles
2. Scope of Contributions
3. Getting Started (Environment & Build)
4. Project Structure
5. Branching & Workflow
6. Issue Workflow
7. Pull Request Guidelines
8. Commit Message Convention
9. Coding Standards
10. Testing Guidelines
11. Documentation & JavaDoc
12. Dependency Policy
13. Versioning & Releases
14. Performance Expectations
15. Internationalization / Language
16. FAQ for Contributors
17. Attribution

---
## 1. Principles
We value: correctness, clarity, minimalism, maintainability, security-by-default, and performance without premature complexity. Every contribution should move at least one of these forward while not regressing the others.

## 2. Scope of Contributions
Acceptable contributions include (but are not limited to):
- Bug fixes & test coverage improvements
- Performance optimizations with measurable benefit
- New configuration providers (storage backends) with generic value
- Validation or serialization helpers
- Documentation, examples, or tutorials
- Tooling that improves developer productivity or release robustness

Out-of-scope (likely to be declined):
- Vendor lock‑in features narrowly targeting one proprietary platform (unless optional & isolated)
- Large feature branches without prior design discussion
- Unbounded abstraction that increases complexity with unclear user value

## 3. Getting Started (Environment & Build)
Requirements:
- JDK 8 (minimum). Later JDKs may work but target bytecode is 1.8.
- Maven 3.8+ (Wrapper optional; project assumes standard mvn).

Build all modules:
```bash
mvn -q clean verify
```
Skip tests (NOT recommended for PR validation):
```bash
mvn -q clean install -DskipTests
```
Run a single module:
```bash
mvn -q -pl core -am test
```
Generate JavaDoc (already bound in build):
```bash
mvn -q javadoc:javadoc
```

## 4. Project Structure (High-level)
- core/ : Fundamental abstractions (Configuration, Value types, factories)
- features/ : Optional, orthogonal enhancements (validators, section, text, etc.)
- providers/ : Concrete persistence / parsing backends (yaml, gson, hocon, sql, mongodb, temp)
- demo/ : Usage demonstrations & sample scenarios

Rules:
- Core must not depend on feature or provider modules.
- Features must not form cycles; prefer depending only on core.
- Providers should keep external dependencies minimal and shaded/isolated only if necessary.

## 5. Branching & Workflow
- main (or master): Stable; only fast‑forward / squash from reviewed PRs.
- feature/<short-name>: New feature work. Open draft PR early for feedback.
- fix/<issue-id>-<slug>: Bug fix referencing an Issue.
- chore/<topic>: Build, infra, docs improvements.

Never force push to main. Force pushes allowed only to your own feature branches.

## 6. Issue Workflow
1. Search existing issues first to avoid duplication.
2. Provide reproduction steps (minimal code or config) for bugs.
3. Label suggestions as enhancement; performance items as perf.
4. For larger features, open a design issue summarizing: Problem, Motivation, Proposed API, Alternatives.

## 7. Pull Request Guidelines
Checklist before opening a PR:
- Linked to at least one Issue (unless trivial doc fix)
- Passes `mvn verify`
- Adds or updates tests covering new behavior / bug
- Includes JavaDoc / README / CHANGELOG fragment if user-facing
- No unrelated refactors or formatting churn
- Minimal diff: avoid reordering imports unless enforced by style

Review expectations:
- Maintainers strive to respond within 5 business days.
- Use constructive, action‑oriented comments.
- Resolve conversations or explain why not.
- Squash commits if they are noisy; retain meaningful logical grouping.

## 8. Commit Message Convention
Use Conventional Commits (https://www.conventionalcommits.org/) with optional scope:
```
<type>(<scope>): <short imperative summary>

<body>(optional)

<footer>(breaking changes, issue references)
```
Types used:
- feat: New feature (user visible)
- fix: Bug fix
- perf: Performance improvement
- refactor: Internal restructuring without behavior change
- docs: Documentation only
- test: Add or fix tests
- build: Build system or dependency changes
- ci: Continuous integration changes
- chore: Maintenance tasks
- style: Formatting (rare; avoid large style‑only changes)

Breaking changes: add `!` after type/scope or include `BREAKING CHANGE:` footer.

## 9. Coding Standards
- Java: Follow effective Java principles; prefer explicit types over inference for public APIs.
- Nullability: Use JetBrains annotations (`@NotNull`, `@Nullable`) where helpful.
- Immutability: Favor immutable value objects; avoid exposing mutable internal state.
- Exceptions: Use specific exception types; no swallowing silently. Validate inputs early.
- Logging: Keep core logging minimal; let consumers decide verbosity. Avoid println.
- APIs: Minimize surface; avoid exposing prematurely general interfaces.
- Annotations: Provide meaningful config path / comments metadata clearly.

### Style
- Indentation: 4 spaces.
- Line length guideline: ≤ 140 chars (soft limit).
- Avoid wildcard imports.

## 10. Testing Guidelines
- Use JUnit (current: JUnit 4). Prefer deterministic, isolated tests.
- Each bug fix must include a regression test failing before the fix.
- Avoid time‑sensitive sleeps; prefer deterministic constructs.
- Keep provider-specific integration tests under provider module.
- Use random data cautiously; if used, log seed for reproduction.

Command:
```bash
mvn -q test
```

## 11. Documentation & JavaDoc
- Public classes & methods: brief JavaDoc describing contract, thread-safety, nullability.
- Add code examples when clarifying complex usage.
- Update README or module README for new feature flags or environment variables.
- Keep demo module aligned with latest recommended usage.

## 12. Dependency Policy
- Keep transitive dependency footprint lean.
- No large frameworks for simple utilities.
- Justify each new dependency in PR description (purpose, size, maintenance risk).
- Prefer stable, well-adopted libraries with permissive licenses compatible with LGPL.
- Security-sensitive libs (parsers, DB drivers) should be periodically updated.

## 13. Versioning & Releases
- Follows Semantic Versioning (MAJOR.MINOR.PATCH).
- Public API additions => MINOR bump.
- Backwards-compatible bug fix => PATCH.
- Backwards-incompatible change => MAJOR (document rationale & migration).
- Release steps (maintainers):
  1. Ensure main is green (CI all passing)
  2. Update CHANGELOG (if present) or Release Notes draft
  3. Bump versions via maven-release-plugin (ensure GPG & staging configured)
  4. Tag + push; verify publication (Central / GitHub Packages)
  5. Publish GitHub Release with highlights + migration notes

## 14. Performance Expectations
- Avoid unnecessary object churn in hot paths.
- Profile before large rewrites. Provide benchmark or allocation stats if claiming improvement.
- Defer I/O and heavy parsing until needed (lazy loading pattern).

## 15. Internationalization / Language
- Primary language: English for code, comments, issues, PRs.
- Chinese clarifications acceptable if accompanied by English.

## 16. FAQ for Contributors
Q: Can I add a new provider?
A: Yes—open a design Issue first summarizing data model, external dependencies, and test strategy.

Q: How do I mark experimental APIs?
A: Add JavaDoc: `@apiNote Experimental: subject to change without notice.` and avoid wide promotion.

Q: Why Java 8 target?
A: Maximizes compatibility across server & embedded environments.

## 17. Attribution
Portions inspired by widely adopted open-source guidelines (Kotlin, Spring, Apache projects) and Conventional Commits.

---
Thank you for helping build a robust configuration ecosystem!

