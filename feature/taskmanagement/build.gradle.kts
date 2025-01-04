plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.taskmanagement"
}

dependencies {
    implementation(projects.taskify.core.domain)
}