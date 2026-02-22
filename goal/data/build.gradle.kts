plugins {
    alias(libs.plugins.runcounter.android.library)
}

android {
    namespace = "com.edu.goal.data"

}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.core)

    implementation(projects.goal.domain)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.domain)
}