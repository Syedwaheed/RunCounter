import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.jvm.library)
}
dependencies{
    implementation(libs.kotlinx.coroutines.core)
}