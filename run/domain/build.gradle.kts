plugins {
    alias(libs.plugins.runcounter.jvm.library)
}
dependencies{
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.domain)
    testImplementation(libs.junit)
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}