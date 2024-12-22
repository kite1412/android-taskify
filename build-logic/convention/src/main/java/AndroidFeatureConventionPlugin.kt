import com.nrr.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("taskify.android.library.compose")
                apply("taskify.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            dependencies {
                "implementation"(project(":core:ui"))

                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }
}