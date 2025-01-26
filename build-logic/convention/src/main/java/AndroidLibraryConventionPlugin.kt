
import com.android.build.gradle.LibraryExtension
import com.nrr.convention.ExtensionType
import com.nrr.convention.configureBuildTypes
import com.nrr.convention.configureKotlinAndroid
import com.nrr.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                configureBuildTypes(
                    commonExtension = this,
                    type = ExtensionType.LIBRARY
                )

                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                    proguardFiles("proguard-rules.pro")
                }

                dependencies {
                    "testImplementation"(libs.findLibrary("junit").get())
                    "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
                    "androidTestImplementation"(libs.findLibrary("androidx.espresso.core").get())
                }
            }
        }
    }
}