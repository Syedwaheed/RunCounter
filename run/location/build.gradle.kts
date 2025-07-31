import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.android.library)
}

android {
    namespace = "com.edu.run.location"
}

dependencies {

    implementation(libs.androidx.core.ktx)


    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.android.gms.play.services.location)

    implementation(projects.core.domain)
    implementation(projects.run.domain)

}