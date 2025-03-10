import com.google.devtools.ksp.gradle.KspExtension
import com.pardess.build_logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            configurations.all {
                exclude(group = "com.intellij", module = "annotations")
            }

            dependencies {
                listOf(
                    "room",
                    "room.runtime",
                    "room.compiler"
                ).forEach { "implementation"(libs.findLibrary(it).get()) }
                "ksp"(libs.findLibrary("room.compiler").get())
                "annotationProcessor"(libs.findLibrary("room.compiler").get())
            }
        }
    }
}
