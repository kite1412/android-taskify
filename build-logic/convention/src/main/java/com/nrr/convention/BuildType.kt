package com.nrr.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    type: ExtensionType
) {
    commonExtension.run {
        when(type) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
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
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        release {
                            configureReleaseBuildType(
                                extensionType = type,
                                commonExtension = commonExtension
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureReleaseBuildType(
    extensionType: ExtensionType,
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    isMinifyEnabled = extensionType == ExtensionType.APPLICATION
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt")
    )
}