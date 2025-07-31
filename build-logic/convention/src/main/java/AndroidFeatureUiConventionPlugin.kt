import com.android.build.gradle.LibraryExtension
import com.edu.convention.ExtensionType
import com.edu.convention.addUiLayerDependency
import com.edu.convention.configureAndroidCompose
import com.edu.convention.configureBuildType
import com.edu.convention.configureKotlinAndroid
import com.edu.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.serialize.codecs.core.NodeOwner
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

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