import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.runcounter.android.application.compose)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.runcounter.jvm.ktor)
    alias(libs.plugins.mapsplatform.secrets.plugin)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.edu.runcounter"


    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {
    //Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //Coil
    implementation(libs.coil.compose)

    //Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    //KotlinxSerialization
    implementation(libs.kotlinx.serialization.json)

    //
    implementation(libs.bundles.koin)

    //Crypto
    implementation(libs.androidx.security.crypto.ktx)
//    implementation(libs.androidx.security.tink.crypto)
    //DataStore
    implementation(libs.androidx.data.store)
    api(libs.core)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Location
    implementation(libs.google.android.gms.play.services.location)

    //Splash Screen
    implementation(libs.androidx.core.splashscreen)

    //Timber
    implementation(libs.timber)

    implementation(projects.core.presentation.ui)
    implementation(projects.core.presentation.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.database)


    implementation(projects.auth.presentation)
    implementation(projects.auth.domain)
    implementation(projects.auth.data)

    implementation(projects.run.presentation)
    implementation(projects.run.domain)
    implementation(projects.run.data)
    implementation(projects.run.network)
    implementation(projects.run.location)


}