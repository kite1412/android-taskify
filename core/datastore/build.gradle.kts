plugins {
    alias(libs.plugins.taskify.android.library)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.datastore"
}

dependencies {
    api(libs.androidx.dataStore)
    api(projects.core.datastoreProto)
    api(projects.core.model)

    androidTestImplementation(libs.kotlinx.coroutines.test)
}