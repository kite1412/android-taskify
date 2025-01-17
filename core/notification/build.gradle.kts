plugins {
    alias(libs.plugins.taskify.android.library)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.notification"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.gson)
}