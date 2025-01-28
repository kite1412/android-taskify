plugins {
    alias(libs.plugins.taskify.android.library)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.summary"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}