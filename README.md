# Taskify
A fully functional Android task management application that delivers reliability, ease of use, and an intuitive design to enhance productivity.

![Android](https://img.shields.io/badge/Android-68c06e?logo=android&style=for-the-badge&logoColor=white) ![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-8b48fc?logo=kotlin&style=for-the-badge&logoColor=white) ![Jetpack Compose Multiplatform](https://img.shields.io/badge/Jetpack%20Compose-4285f4?logo=jetpack-compose&style=for-the-badge&logoColor=white)<br/>

## ✨ Features

✅ **Smart Task Categorization** – Organize tasks into categories for better clarity.

⏰ **Timely Notifications** – Get reminders exactly when needed to stay on top of important tasks.

📊 **Productivity Insights** – View **daily, weekly, and monthly summaries** to track progress and efficiency.

📱 **Modern UI with Jetpack Compose** – A clean, intuitive, and fluid interface for a seamless experience.

## 📥 Installation
### 🔹 Requirements
- Android Studio
- Android device running **Android 7.0 (Nougat) or higher**

### 🔹 Debug Build
1. Clone the repository

    ```bash
    git clone https://github.com/kite1412/android-taskify.git
    ```
2. Open the downloaded repository in Android Studio
3. Simply click run (▶️) button at the top of the Android Studio to install and launch the app.

### 🔹 Release Build (preferred for smaller apk size)
1. Clone the repository

    ```bash
    git clone https://github.com/kite1412/android-taskify.git
    ```
2. Open the downloaded repository in Android Studio
3. Navigate to **Build > Generate Signed APP Bundle / APK**
4. Select **APK** to build signed APK
5. Choose an existing `.jks` file or create a new one to sign the APK
6. Select **release** as Build Variants then **Create**, wait for the APK file generation to complete
7. In project's root directory, run  following command:

    ```bash
    adb install app/release/app-release.apk
    ```