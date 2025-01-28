plugins {
    alias(libs.plugins.taskify.android.library)
}

android {
    namespace = "com.nrr.domain"
}

dependencies {
    api(projects.core.data)

    implementation(projects.core.notification)

    implementation(libs.javax.inject)
}