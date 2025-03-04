plugins {
    alias(libs.plugins.taskify.android.feature)
}

android {
    namespace = "com.nrr.analytics"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.chart)
}