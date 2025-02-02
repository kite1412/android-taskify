plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.summaries"
}

dependencies {
    implementation(projects.core.domain)
}