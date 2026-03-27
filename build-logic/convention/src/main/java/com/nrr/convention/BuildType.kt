package com.nrr.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension,
    type: ExtensionType
) {
    when(type) {
        ExtensionType.APPLICATION -> {
            (commonExtension as ApplicationExtension).apply {
                buildTypes {
                    release {
                        configureReleaseBuildType(
                            extensionType = type,
                            commonExtension = commonExtension
                        )
                    }
                    debug {
                        applicationIdSuffix = ".debug"
                        versionNameSuffix = "-debug"
                    }
                }
            }
        }
        ExtensionType.LIBRARY -> {
            (commonExtension as LibraryExtension).apply {
                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
                    }
                }
            }
        }
    }
}

private fun BuildType.configureReleaseBuildType(
    extensionType: ExtensionType,
    commonExtension: CommonExtension
) {
    isMinifyEnabled = extensionType == ExtensionType.APPLICATION
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt")
    )
}