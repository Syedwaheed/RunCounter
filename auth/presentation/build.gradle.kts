import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.android.feature.ui)

}

android {
    namespace = "com.edu.auth.presentation"

}

dependencies {
    implementation(projects.auth.domain)
    implementation(projects.core.domain)
}