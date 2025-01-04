plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.taskdetail"
}

dependencies {
    implementation(projects.taskify.core.domain)
}