# Build APK using only an Android phone

This project can be built in GitHub Actions without installing Android Studio on the phone.

1. Create/sign in to a GitHub account in your phone browser.
2. Create a new public or private repository named `jadur-kathi-android`.
3. Extract this ZIP on your phone. Upload the CONTENTS of the `jadur_kathi_android` folder to the repository root, preserving folders (including `.github/workflows/build-apk.yml`). If GitHub's mobile upload flow cannot upload folders, use GitHub's web editor or a Git client app that supports uploading folders.
4. Open the repository's Actions tab and select `Build Jadur Kathi APK`.
5. Tap `Run workflow`. Wait for the workflow to finish successfully.
6. Open the completed workflow run, find `Artifacts`, and download `jadur-kathi-debug-apk`.
7. Extract the artifact ZIP and tap `app-debug.apk` to install it. Android may ask you to allow your browser/files app to install unknown apps; only enable this for the app you trust and turn it off again afterwards.

This creates a debug/testing APK, not a production-ready app. The current project is a UI prototype; real accounts, database, video storage, ordering, payments, and admin security still need backend implementation before public launch.
