import com.android.build.gradle.LibraryExtension
import com.nrr.convention.ExtensionType
import com.nrr.convention.configureBuildTypes
import com.nrr.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                configureBuildTypes(
                    commonExtension = this,
                    type = ExtensionType.LIBRARY
                )

                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
        }
    }
}