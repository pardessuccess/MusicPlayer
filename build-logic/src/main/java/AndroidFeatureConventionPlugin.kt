import com.android.build.gradle.LibraryExtension
import com.pardess.build_logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply {
                apply("pardess.android.library")
                apply("pardess.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
            }

            dependencies {
                listOf(
                    ":core:ui",
                    ":core:common",
                    ":core:domain",
                    ":core:model",
                    ":core:designsystem",
                    ":core:navigation",
                ).forEach { "implementation"(project(it)) }

                listOf(
                    "hilt.navigation.compose",
                    "androidx.lifecycle.runtime.ktx",
                    "kotlinx.serialization.json",
                    "lazycolumnscrollbar"
                ).forEach { "implementation"(libs.findLibrary(it).get()) }
            }
        }
    }
}
