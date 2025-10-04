plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.schedule"
}

dependencies {
    implementation(projects.taskify.core.domain)
}