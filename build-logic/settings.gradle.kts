pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    versionCatalogs{
        create("libs"){
            from(files("../gradle/libs.versions.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "build-logic"
include(":convention")