plugins {
    alias(libs.plugins.taskify.android.application.compose)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.taskify"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.data)

    implementation(projects.feature.todayplan)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}