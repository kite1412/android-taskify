plugins {
    alias(libs.plugins.taskify.android.room)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.database"
}

dependencies {
    api(projects.taskify.core.model)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}