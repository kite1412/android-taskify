plugins {
    alias(libs.plugins.taskify.android.application.compose)
    alias(libs.plugins.taskify.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nrr.taskify"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(projects.summary)

    implementation(projects.feature.todayplan)
    implementation(projects.feature.registration)
    implementation(projects.feature.taskmanagement)
    implementation(projects.feature.taskdetail)
    implementation(projects.feature.plandetail)
    implementation(projects.feature.planarrangement)
    implementation(projects.feature.settings)
    implementation(projects.feature.summaries)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.work)
    implementation(libs.androidx.core.splashscreen)
    // TODO remove later
    implementation(libs.kotlinx.serialization.json)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}