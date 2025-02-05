
import androidx.room.gradle.RoomExtension
import com.android.build.gradle.LibraryExtension
import com.nrr.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.google.devtools.ksp")
                apply("androidx.room")
                apply("taskify.android.library")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }
            extensions.configure<LibraryExtension> {
                sourceSets.apply {
                    getByName("androidTest").assets.srcDirs("$projectDir/schemas")
                }
            }

            dependencies {
                "implementation"(libs.findLibrary("room.runtime").get())
                "implementation"(libs.findLibrary("room.ktx").get())
                "ksp"(libs.findLibrary("room.compiler").get())
            }
        }
    }
}