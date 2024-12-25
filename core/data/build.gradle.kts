plugins {
    alias(libs.plugins.taskify.android.library)
    alias(libs.plugins.taskify.hilt)
}

android {
    namespace = "com.nrr.data"
}

dependencies {
    api(projects.core.database)
    api(projects.core.datastore)
}