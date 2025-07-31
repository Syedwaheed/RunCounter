import com.android.build.gradle.LibraryExtension
import com.edu.convention.ExtensionType
import com.edu.convention.configureBuildType
import com.edu.convention.configureKotlinAndroid
import com.edu.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.serialize.codecs.core.NodeOwner
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run{
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension>{
                configureKotlinAndroid(this)
                configureBuildType(
                    commonExtension = this,
                    extensionType =  ExtensionType.LIBRARY
                )
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
            dependencies{
                "testImplementation"(kotlin("test"))
            }
        }
    }
}