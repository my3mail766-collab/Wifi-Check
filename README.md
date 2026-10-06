# Wi-Fi Scanner Android

Aplikasi Android Kotlin sederhana untuk menampilkan jaringan Wi-Fi yang terdeteksi perangkat.

## Fitur
- Scan Wi-Fi
- SSID
- BSSID
- Signal/dBm
- Frequency/MHz
- Perkiraan tipe security

## Build lokal
Gunakan Gradle/JDK 17.

## Build di GitHub
Project sudah dilengkapi `.github/workflows/build-apk.yml`.

Push ke branch `main`, lalu buka:
**GitHub → Actions → Build Android APK**

Hasil APK tersedia pada bagian **Artifacts** dari workflow.

> Catatan: Android dapat membatasi Wi-Fi scanning dan memerlukan permission/location service sesuai versi Android.
