pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "komik-ai"

// NOTE: nama `dev.komik.ai` = placeholder. Setelah keputusan nama, ganti + rename folder
include(
    ":app",
    ":core:database",
    ":core:designsystem",
    ":feature:library",
    ":feature:reader",
    ":feature:translate",
    ":engine",
)
project(":engine").projectDir = file("../engine")
