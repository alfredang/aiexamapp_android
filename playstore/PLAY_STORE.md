# Google Play submission guide — AI Exams (Android)

Everything needed to publish `com.tertiaryinfotech.aiexams` to Google Play.

## Build artifact

```
app/build/outputs/bundle/release/app-release.aab    (signed, ~3.5 MB)
```

Rebuild any time with:

```sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:bundleRelease
```

- **Application ID:** `com.tertiaryinfotech.aiexams`
- **versionCode:** 1 · **versionName:** 1.0
- **Min SDK:** 24 (Android 7.0) · **Target SDK:** 35 (Android 15)
- **Upload key (SHA-256):** `66:05:05:C4:56:4B:FB:0C:9B:D3:27:C6:6A:9C:E0:B0:E4:6A:AF:73:39:2B:34:8B:E8:7A:2B:34:70:24:11:71`

> ⚠️ Keep `aiexamapp_androiid/aiexams-upload.jks` + `keystore.properties` safe and private (they are git-ignored). You need them for every future update.

## Store assets (in this folder)

| Asset | File | Play requirement |
| ----- | ---- | ---------------- |
| App icon | `icon-512.png` | 512×512 PNG (32-bit) ✓ |
| Feature graphic | `feature-graphic-1024x500.png` | 1024×500 PNG ✓ |
| Phone screenshots | _to capture_ | 2–8 images, 16:9 or 9:16, min 320px — see below |

### Screenshots (must be captured from a running build)

Play requires at least 2 phone screenshots. Capture real ones (Play rejects placeholders):

```sh
# Start an emulator (or plug in a device), then:
adb shell screencap -p /sdcard/s1.png && adb pull /sdcard/s1.png
```

Capture: **My Exams**, **Catalog**, a **bundle detail**, the **exam runner** (a question), and a **scored result**.

## Store listing copy

**App name:** AI Exams

**Short description (≤80 chars):**
> Practice certification exams on the go — Practice & Exam modes with scoring.

**Full description:**
> AI Exams is the official mobile practice client for Tertiary AI Exams. Sign in
> with your existing account to practice the certification exams you already own.
>
> • Browse the full practice-exam catalog
> • Try free teaser exams from any bundle
> • Open your purchased exams in Practice mode (with explanations after each
>   answer) or Exam mode (timed, scored on submission)
> • Track your score and per-domain results
>
> Purchases, payments, invoices, and vouchers are handled on the Tertiary Exams
> website. This app is for mobile practice only.

**Category:** Education · **Contact email:** angch@tertiaryinfotech.com
**Website:** https://exams.tertiaryinfotech.com

## Data safety form (answers)

- Collects **Email address** and **Name** → for App functionality / Account management. Required.
- Auth token stored **on-device** (DataStore), transmitted over HTTPS.
- No location, no ads, no third-party sharing, no analytics SDKs.
- Account deletion available **in-app** (Account → Delete Account) and is the URL
  to provide for the "account deletion" requirement.

## Content rating questionnaire

Education app, no objectionable content → expect **Everyone / PEGI 3**. Answer "No"
to all violence/sexual/gambling/controlled-substance questions.

## Step-by-step upload

1. Go to <https://play.google.com/console> (one-time $25 developer registration if not done).
2. **Create app** → name "AI Exams", language English (US), App, Free.
3. **Set up your app** checklist:
   - App access → "All functionality available without restrictions"? No — provide a
     **test login** (a real account on the backend) so reviewers can sign in.
   - Ads → No. Content rating → fill questionnaire above. Target audience → 18+ (or as desired).
   - Data safety → enter the answers above. Privacy policy → host one at a public URL
     (e.g. on the website) and paste the link.
4. **Production → Create new release**:
   - Let Google **manage app signing** (recommended). Upload `app-release.aab`.
   - Paste release notes (e.g. "Initial release.").
5. **Main store listing**: paste the copy above, upload `icon-512.png`,
   `feature-graphic-1024x500.png`, and ≥2 phone screenshots.
6. **Review release → Start rollout to Production**. The app enters Google review.

## Why this last step is manual

Uploading "for review" is a credentialed action tied to **your** Google Play Console
account. It cannot be automated without either signing into the Console UI or
configuring a Play Developer API service account with the app already created. Once
the app exists in the Console, future uploads can be automated (e.g. via fastlane
`supply` or the `ios-auto-release`-style CI pattern adapted for Android).
