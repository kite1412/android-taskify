plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.plandetail"
}

dependencies {
    implementation(projects.core.domain)
}