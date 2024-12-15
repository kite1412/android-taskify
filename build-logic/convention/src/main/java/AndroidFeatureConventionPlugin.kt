import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("taskify.android.library.compose")
            }
            dependencies {
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:ui"))
            }
        }
    }
}