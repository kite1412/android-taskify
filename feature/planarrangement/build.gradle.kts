plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.planarrangement"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.cronet.embedded)
}