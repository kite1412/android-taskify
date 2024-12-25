plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.todayplan"
}

dependencies {
    implementation(projects.core.domain)
}