import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.android.library)
    alias(libs.plugins.runcounter.jvm.ktor)
}

android {
    namespace = "com.edu.auth.data"
}

dependencies {
    implementation(projects.auth.domain)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(libs.bundles.koin)
}