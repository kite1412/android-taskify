plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.planarrangement"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.notification)
    implementation(libs.cronet.embedded)
    implementation(libs.androidx.activity.compose)
}