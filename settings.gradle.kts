pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://jitpack.io")
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "MusicPlayer"
include(":app")
include(":core")
include(":feature")
include(":feature:home")
include(":core:model")
include(":core:common")
include(":core:data")
include(":core:datastore")
include(":core:database")
include(":core:designsystem")
include(":core:domain")
include(":core:media-query")
include(":core:media-service")
include(":core:ui")
include(":feature:artist")
include(":feature:playlist")
include(":feature:songs")
include(":feature:playback")
include(":core:navigation")
include(":feature:root")
