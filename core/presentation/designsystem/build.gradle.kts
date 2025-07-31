import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.android.library.compose)
}

android {
    namespace = "com.edu.core.presentation.designsystem"

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material3.adaptive)

    api(libs.androidx.material3)
    implementation(platform(libs.androidx.compose.bom))
}