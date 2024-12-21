plugins {
    alias(libs.plugins.taskify.android.library.compose)
}

android {
    namespace = "com.nrr.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.adaptive)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}