plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "taskify.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidApplicationCompose") {
            id = "taskify.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("androidLibrary") {
            id = "taskify.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidLibraryCompose") {
            id = "taskify.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        register("androidFeature") {
            id = "taskify.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("jvmLibrary") {
            id = "taskify.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }

        register("hilt") {
            id = "taskify.hilt"
            implementationClass = "HiltConventionPlugin"
        }

        register("androidRoom") {
            id = "taskify.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
    }
}