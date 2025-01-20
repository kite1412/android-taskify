plugins {
    alias(libs.plugins.taskify.android.library)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.notification"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.gson)

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}