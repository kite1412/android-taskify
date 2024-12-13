plugins {
    alias(libs.plugins.taskify.android.library.compose)
}

android {
    namespace = "com.nrr.designsystem"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.material3)
}