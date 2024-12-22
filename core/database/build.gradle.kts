plugins {
    alias(libs.plugins.taskify.android.room)
}

android {
    namespace = "com.nrr.database"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}