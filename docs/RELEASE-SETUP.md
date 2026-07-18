# Release credentials — one-time setup

A start-to-finish walkthrough for provisioning the credentials that let this
repo publish to Maven Central: a **Central Portal publishing token** and a
**GPG signing key**, wired into GitHub Actions secrets, then backed up and
cleaned up safely.

You only do this once (per maintainer/key). Day-to-day releasing is in
[`PUBLISHING.md`](./PUBLISHING.md).

> **Never commit or paste any of these values into the repo, a chat, or a
> shared doc.** The steps below keep every secret on your machine or in
> write-only stores (GitHub Secrets, your password manager).

---

## 0. Prerequisites

- The `dev.oasp` namespace is **verified** on [central.sonatype.com](https://central.sonatype.com) (DNS TXT on `oasp.dev`).
- `gpg` installed locally (`gpg --version`).
- `gh` CLI authenticated against the repo (`gh auth status`).

---

## 1. Central Portal publishing token (username + password)

1. Sign in to [central.sonatype.com](https://central.sonatype.com) → your account → **Generate User Token**.
2. Name it for its use, e.g. `oasp-java-sdk-release`. Set it to **Does Not Expire** — it lives only in GitHub Secrets, and a silent expiry would break releases; revoke it if it's ever leaked.
3. It shows a **username** and **password** pair. Copy both now — the password is shown once.

These become the `MAVEN_CENTRAL_USERNAME` / `MAVEN_CENTRAL_PASSWORD` secrets (step 3).

---

## 2. GPG signing key

Central rejects unsigned artifacts. Generate a key **in your own environment** —
the private key + passphrase are your project's signing identity and must never
pass through a third party.

```bash
gpg --full-generate-key
```
Answer the prompts:

| Prompt | Answer |
|--------|--------|
| Kind of key | `1` (RSA and RSA) |
| Keysize | `4096` |
| Valid for | `2y` (extendable later; `0` = never is also fine) |
| Real name | your name (public on the signature) |
| Email | an address you control |
| Comment | *(blank)* |
| Passphrase | a strong one — this is `SIGNING_KEY_PASSWORD` |

Find the **fingerprint** (the 40-char hex line directly under the `sec … [SC]`
line — *not* the `ssb`/`[E]` subkey):

```bash
gpg --list-secret-keys --keyid-format=long
```

Publish the **public** key so Central can verify signatures, and export the
**private** key for CI:

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys <FINGERPRINT>
gpg --export-secret-keys --armor <FINGERPRINT> > /tmp/oasp-signing.asc
```

`/tmp/oasp-signing.asc` now holds the armored private key (the whole
`-----BEGIN … END PGP PRIVATE KEY BLOCK-----`). Handle it carefully — you'll
delete it in step 5.

---

## 3. GitHub Actions secrets

Four repo secrets (Settings → Secrets and variables → Actions), matching
[`release.yml`](../.github/workflows/release.yml). GitHub secrets are
**write-only** — you can update but never read them back.

| Secret | Value |
|--------|-------|
| `MAVEN_CENTRAL_USERNAME` | token username (step 1) |
| `MAVEN_CENTRAL_PASSWORD` | token password (step 1) |
| `SIGNING_KEY` | the full armored private key from `/tmp/oasp-signing.asc` |
| `SIGNING_KEY_PASSWORD` | the GPG passphrase (step 2) |

Via the UI (click each → **Update** → paste), or the CLI in your terminal:

```bash
R=oasp-dev/oasp-java-sdk
printf '%s' 'TOKEN_USERNAME'  | gh secret set MAVEN_CENTRAL_USERNAME  -R $R
printf '%s' 'TOKEN_PASSWORD'  | gh secret set MAVEN_CENTRAL_PASSWORD  -R $R
printf '%s' 'GPG_PASSPHRASE'  | gh secret set SIGNING_KEY_PASSWORD    -R $R
gh secret set SIGNING_KEY -R $R < /tmp/oasp-signing.asc   # multi-line: read from the file
```

`SIGNING_KEY` must keep its line breaks — read it from the file (or paste in
the UI); a one-line `printf` would mangle the armored block.

---

## 4. Back up & store (before you delete anything)

The GitHub secret is **not** a retrievable backup. You will need the private key
again — to extend its expiry (~2y), to revoke it, or to sign from elsewhere.

1. Store the **private key + passphrase** in a durable, secure place — a password
   manager (1Password / Bitwarden) or an encrypted offline backup.
2. Generate and safely store a **revocation certificate** so you can revoke the
   key even if you lose it:
   ```bash
   gpg --gen-revoke <FINGERPRINT> > oasp-revoke.asc
   # move oasp-revoke.asc into your password manager, then delete the file
   ```

---

## 5. Clean up

```bash
rm -f /tmp/oasp-signing.asc     # the exported private key on disk
rm -f oasp-revoke.asc           # once it's safely in your password manager
```

The key in your local `~/.gnupg` keyring you **may** keep (it's encrypted at
rest by your passphrase, and it's what you'll use to extend the expiry) or, once
steps 4 are done, remove for a clean machine:

```bash
gpg --delete-secret-keys <FINGERPRINT>   # optional; only after a secure backup exists
```

---

## 6. Release

Once the four secrets show fresh timestamps: bump `version` in the root
`build.gradle.kts`, then

```bash
git tag v0.1.0-alpha.0 && git push origin v0.1.0-alpha.0
```

The `release` workflow publishes to the Central Portal, where the deployment
lands **validated/pending** for you to finalise (see [`PUBLISHING.md`](./PUBLISHING.md)).

---

## Maintenance

- **Extend the key expiry** before it lapses: `gpg --edit-key <FINGERPRINT>` → `expire` → `save`, then re-export and update the `SIGNING_KEY` secret.
- **Rotate the Central token**: generate a new one, update the two secrets, revoke the old.
- **Compromise**: publish the revocation certificate (`gpg --import oasp-revoke.asc && gpg --send-keys <FINGERPRINT>`), revoke the Central token, rotate both.
