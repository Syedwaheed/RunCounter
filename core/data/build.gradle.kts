import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.android.library)
    alias(libs.plugins.runcounter.jvm.ktor)
}

android {
    namespace = "com.edu.core.data"
}

dependencies {
    implementation(libs.timber)
    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation(libs.bundles.koin)
    implementation(libs.play.services.auth)

}
