# AI Exams — Android

Native **Kotlin + Jetpack Compose** client for Tertiary AI Exams mobile practice — the Android counterpart to the [iOS app](https://github.com/alfredang/aiexamapp).

It talks to the same backend (`https://exams.tertiaryinfotech.com`) and mirrors every feature of the iOS client.

## Scope

- Register and sign in with email/password.
- Browse the public exam catalog without checkout or payment actions.
- View purchased practice exams from the user's existing entitlements ("My Exams").
- Start free teasers from catalog entries.
- Start purchased exams in **Practice** mode or **Exam** mode.
- Save answers, reveal explanations in Practice mode, and submit for scoring.
- Account management: open the website, sign out, delete account.

> Purchases, payments, invoices, and vouchers stay on the website. This app is for mobile practice only.

## Tech stack

| Concern        | Choice |
| -------------- | ------ |
| Language       | Kotlin 2.0 |
| UI             | Jetpack Compose + Material 3 |
| Navigation     | Navigation Compose (bottom tabs + nested routes) |
| Networking     | Retrofit 2.11 + OkHttp |
| JSON           | kotlinx.serialization |
| Persistence    | DataStore (auth token + user) |
| Min / Target   | Android 7.0 (API 24) / Android 15 (API 35) |

## Architecture

```
data/        ApiModels · ApiService (Retrofit) · ApiClient · SessionStore (DataStore)
ui/          SessionViewModel · AppRoot (Scaffold + NavHost)
ui/theme/    Brand palette ported from the iOS Theme
ui/screens/  Auth · Catalog (+ detail) · Library · StartExam · ExamRunner · Account
```

The DTOs in `data/ApiModels.kt` match the `/api/mobile/*` JSON contract exactly, so the same backend serves both the iOS and Android apps.

## Build

```sh
# Uses the Android Studio bundled JDK 21
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Debug APK
./gradlew :app:assembleDebug

# Signed release App Bundle (requires keystore.properties + a keystore — see below)
./gradlew :app:bundleRelease
```

### Release signing

Signing config is read from `keystore.properties` at the project root (git-ignored):

```properties
storeFile=aiexams-upload.jks
storePassword=********
keyAlias=aiexams
keyPassword=********
```

Generate an upload keystore once:

```sh
keytool -genkeypair -v -keystore aiexams-upload.jks -alias aiexams \
  -keyalg RSA -keysize 2048 -validity 10000
```

Keep the keystore and `keystore.properties` private — they are required to publish updates and are intentionally excluded from version control.

## Google Play

The release artifact is an Android App Bundle at:

```
app/build/outputs/bundle/release/app-release.aab
```

Upload it in the [Play Console](https://play.google.com/console) under your app → **Production → Create new release**. Play App Signing manages the final distribution key; the keystore above is your upload key.

## License

Proprietary © Tertiary Infotech.
