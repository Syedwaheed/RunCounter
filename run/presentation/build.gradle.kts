plugins {
    alias(libs.plugins.runcounter.android.feature.ui)
    alias(libs.plugins.mapsplatform.secrets.plugin)
}

android {
    namespace = "com.edu.run.presentation"
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "secrets.defaults.properties"
    // Look for properties files in the root project directory
    ignoreList.add("sdk.*")
}

dependencies {

    implementation(libs.coil.compose)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
    implementation(libs.vico.compose.m3)
    implementation(libs.androidx.material.icons.extended)
    implementation(projects.run.domain)
    implementation(projects.core.domain)
    implementation(projects.goal.domain)
}