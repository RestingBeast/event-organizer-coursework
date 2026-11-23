# Event Manager (COMP1786)

This repository has two apps that share one Firebase Realtime Database:

| Folder | App | Built with |
|---|---|---|
| `admin-app/` | Admin app. Organisers add, edit and delete events and sessions, then upload them to Firebase. | Kotlin, Android (SQLite) |
| `user-app/` | User app. Attendees browse and search events, view sessions and save favourites. | Expo (React Native, TypeScript) |

## Requirements

- Android Studio with the Android SDK (API 37) and an emulator, or an Android phone with USB debugging turned on
- JDK 17 or newer (Android Studio includes one)
- Node.js 20 or newer, with npm
- Expo Go on the emulator or phone, only needed to run the user app the quick way

## Admin app (Kotlin)

### Run in Android Studio

1. Open the `admin-app` folder in Android Studio (**File → Open**).
2. Wait for the Gradle sync to finish.
3. Choose an emulator or phone and press **Run ▶**.

### Build from the command line

```bash
cd admin-app
./gradlew assembleDebug      # builds the APK
./gradlew installDebug       # installs it on the connected emulator or phone
```

The APK is saved to `admin-app/app/build/outputs/apk/debug/app-debug.apk`.

## User app (Expo)

### Install dependencies

```bash
cd user-app
npm install
```

### Run with Expo Go

```bash
npx expo start
```

Then press `a` to open the app on the Android emulator, or scan the QR code with Expo Go on a phone.

### Build a native Android app (optional)

```bash
npx expo run:android
```

This creates the `android/` folder, compiles the app with Gradle and installs it on the emulator or phone.

### Check types

```bash
npx tsc --noEmit
```

## Firebase

Both apps use the same database URL. The admin app sets it in `admin-app/app/src/main/java/com/example/eventadmin/CloudSync.kt` and the user app sets it in `user-app/src/lib/api.ts`.

- The admin app uploads all events and their sessions with one replace-all request.
- The user app only reads that data.

To see data in the user app:

1. Add events in the admin app.
2. Tap the upload button.
3. Pull down to refresh in the user app.

The database uses test-mode rules, which stop allowing reads and writes on their expiry date. If uploads or loading start to fail, extend the rules in the Firebase console.
