import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.android.library)
    alias(libs.plugins.runcounter.jvm.ktor)
}

android {
    namespace = "com.edu.run.network"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)

}