# Security Policy

English is the authoritative language of this document.

## Supported Versions

We generally provide security fixes only for the latest released MINOR version (most recent tag on the default branch). Older versions may receive fixes only if the vulnerability is critical and a patch is low risk.

| Version  | Status                  |
|----------|-------------------------|
| Latest   | Security fixes          |
| < Latest | Not routinely supported |

If you rely on an older version you are strongly encouraged to upgrade promptly after each release.

## Reporting a Vulnerability

Please DO NOT open a public Issue for suspected security problems.

Instead, email: carm@carm.cc with:

- A clear description of the issue and potential impact
- Steps to reproduce (minimal code / configuration)
- Affected version(s) and environment (JDK, OS)
- Any known workarounds
- Preferred public credit name (optional)

You will receive an acknowledgement within 72 hours (workdays) confirming receipt.

## Assessment & Disclosure Process

1. Triage & validation (attempt reproduction, scope impact)
2. Determine severity (CVSS or qualitative: Low / Moderate / High / Critical)
3. Prepare a private fix / patch + regression tests
4. Coordinate an embargoed release window (typically ≤14 days after validation for High/Critical)
5. Release a new version (and possibly backport if warranted)
6. Publish security advisory (GitHub Security Advisory + Release Notes) including mitigation steps

We may reject reports that are clearly non‑security bugs (e.g., feature requests, performance tuning) or issues requiring unreasonable preconditions (e.g., attacker already has full local code execution).

## Non-Qualifying Issues (Examples)

- Missing rate limits on non-authenticated, non-state-changing operations
- Denial-of-service requiring unrealistic resource constraints or already solved via JVM flags
- Vulnerabilities only exploitable on unsupported / EOL Java versions
- Social engineering, SPF/DMARC issues beyond this codebase’s control

## Coordinated Disclosure

If you plan to blog or speak publicly about the vulnerability prior to patch availability, please coordinate timing so users can upgrade safely.

## Dependency Security

We periodically review dependency versions for CVEs. You can help by:

- Submitting PRs that upgrade vulnerable libraries with changelog & compatibility notes
- Avoiding unnecessary new dependencies

## Cryptographic Material

This project does not bundle custom cryptographic primitives. If you discover misuse of crypto APIs or insecure random number use in security-sensitive areas, treat it as a security report.

## Reporting Format Template (Recommended)

```
Subject: [Security Report] <short title>

Affected Component: (module / class)
Version(s) Tested: x.y.z (and earlier if known)
Environment: JDK x, OS

Summary:
Describe the vulnerability and impact.

Reproduction Steps:
1. ...
2. ...
3. ...

Expected vs Actual:

Potential Impact:

Workarounds / Mitigations (if any):

Credit: (name / handle / anonymous)
```

## Credit & Acknowledgement

We will list (with permission) reporters who submit valid, first responsibly disclosed security issues in the release notes / advisory.

## GPG / Integrity

Release artifacts are signed (see project docs). Always verify signatures and checksums when consuming artifacts from Maven Central or GitHub Packages.

## Questions

For general (non-sensitive) questions, open an Issue labeled `question` rather than using the security email.

Thank you for helping keep the ecosystem safe.

