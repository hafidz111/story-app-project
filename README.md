# 📸 Story App
Aplikasi Android untuk berbagi cerita berupa foto dan deskripsi, dibuat menggunakan **Jetpack Compose** dan **API Story dari Dicoding**.

---

## 🖼️ Tampilan Aplikasi
Light Mode
![LightMode story app](https://github.com/user-attachments/assets/31e330eb-a52c-46ab-8b66-e396177d065f)

Dark Mode
![Darkmode story app](https://github.com/user-attachments/assets/42ba9dda-dc68-4642-9938-6cb861228032)

---

## ✨ Fitur Utama
1. 🔗 Menggunakan API resmi dari Dicoding:  
   [`https://story-api.dicoding.dev/v1/`](https://story-api.dicoding.dev/v1/)
2. ⏳ Splash Screen: Tampilan pembuka aplikasi
3. 🔐 Fitur autentikasi:
   - Login
   - Register
   - Logout
4. 🗃️ Menyimpan sesi dan token pengguna menggunakan **DataStore Preferences**
5. 📜 Menampilkan daftar cerita dengan Paging 3 dan halaman detail cerita ketika di klik Load more
6. 📷 Menambahkan cerita dengan:
   - Mengambil gambar dari galeri
   - Mengambil gambar dari kamera
7. 💬 Komentar pada cerita:
   - Menampilkan komentar dalam Bottom Sheet dan di dalam halaman detail cerita
   - Mengirim dan membaca komentar secara real-time menggunakan Firebase Realtime Database
8. 🗺️ Menampilkan lokasi cerita menggunakan Google Maps dan marker lokasi
9. 🌙 Mendukung Dark Mode yang disimpan sebagai preferensi pengguna
10. 🧠 Menggunakan **Property Animation** pada elemen UI
11. ℹ️ Menampilkan informasi selama proses komunikasi dengan API (loading, error, dll.)
12. 🏛️ Menerapkan **Android Architecture Component**:
    - ViewModel
    - LiveData / StateFlow
13. 🧪 Menggunakan arsitektur bersih:
    - **Repository Pattern**
    - **Dependency Injection**
14. 🔄 Menggunakan **Kotlin Coroutines** untuk proses asynchronous

---

## 📦 Teknologi
- Kotlin
- Jetpack Compose
- Jetpack SplashScreen API
- Retrofit + OkHttp
- DataStore Preferences
- CameraX
- Navigation Compose
- ViewModel + LiveData + Flow
- Coil
- Google Maps SDK
- Paging 3
- Firebase Realtime Database

---

## 📥 Cara Install APK
1. Download file APK di sini 👉 [Story App.apk](https://github.com/hafidz111/story-app-project/releases/download/v1.0.1/Story-App.v1.0.1.apk)
2. Install di perangkat Android atau emulator
3. Buka aplikasi yang telah terpasang
