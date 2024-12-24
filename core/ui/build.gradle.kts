plugins {
    alias(libs.plugins.taskify.android.library.compose)
}

android {
    namespace = "com.nrr.ui"
}

dependencies {
    api(projects.taskify.core.designsystem)
    api(projects.taskify.core.model)
    implementation(libs.cronet.embedded)
}