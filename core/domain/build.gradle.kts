plugins {
    alias(libs.plugins.taskify.android.library)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.domain"
}

dependencies {
    api(projects.core.data)
}