plugins {
    alias(libs.plugins.runcounter.jvm.library)
}
dependencies{
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.domain)

}