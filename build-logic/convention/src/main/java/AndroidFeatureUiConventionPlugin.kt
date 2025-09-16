import com.edu.convention.addUiLayerDependency
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureUiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run{
                apply("runcounter.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")

            }
            dependencies{
                addUiLayerDependency(target)
            }

        }
    }
}