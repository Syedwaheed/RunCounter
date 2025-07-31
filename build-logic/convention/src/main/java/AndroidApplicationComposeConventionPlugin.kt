import com.android.build.api.dsl.ApplicationExtension
import com.edu.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {

        target.run {
            pluginManager.apply("runcounter.android.application")
            val extensions = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extensions)
        }
    }

}