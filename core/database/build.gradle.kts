plugins {
    alias(libs.plugins.taskify.android.room)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.database"
}

dependencies {
    api(projects.core.model)

    testImplementation(libs.junit)

    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}