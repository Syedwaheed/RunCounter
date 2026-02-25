plugins {
    alias(libs.plugins.runcounter.android.feature.ui)
}

android {
    namespace = "com.edu.goal.presentation"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
    implementation(libs.kotlinx.collections.immutable)

    implementation(projects.core.domain)
    implementation(projects.goal.domain)
}