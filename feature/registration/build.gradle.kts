plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.registration"
}

dependencies {
    implementation(projects.core.domain)
}