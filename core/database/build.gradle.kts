plugins {
    alias(libs.plugins.taskify.android.room)
}

android {
    namespace = "com.nrr.database"
}

dependencies {
    api(projects.taskify.core.model)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}