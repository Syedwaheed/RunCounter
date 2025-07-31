package com.edu.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.ProguardFiles.getDefaultProguardFile
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildType(
    commonExtension: CommonExtension<*, *, *, *, *,*>,
    extensionType: ExtensionType
){
    commonExtension.run {

        buildFeatures{
            buildConfig = true
        }
        val apiKey = gradleLocalProperties(rootDir,providers).getProperty("API_KEY")
        when(extensionType){
            ExtensionType.APPLICATION ->{
                extensions.configure<ApplicationExtension>{

                    buildTypes{
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension,apiKey)
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension>{

                    buildTypes{
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension,apiKey)
                        }
                    }
                }
            }
        }

    }
}

private fun BuildType.configureDebugBuildType(apikey:String?){
    buildConfigField("String","API_KEY","\"$apikey\"")
    buildConfigField("String","BASE_URL","\"https://runique.pl-coding.com:8080\"")
}
private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*,*,*,*,*,*>,
    apikey:String?
){
    buildConfigField("String","API_KEY","\"$apikey\"")
    buildConfigField("String","BASE_URL","\"https://runique.pl-coding.com:8080\"")
    isMinifyEnabled = true
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}