plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.settings"
}

dependencies {
    implementation(projects.core.data)
}