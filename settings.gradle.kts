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

rootProject.name = "Taskify"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:database")
include(":core:designsystem")
include(":core:ui")
include(":core:domain")
include(":core:model")
include(":core:data")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:notification")
include(":feature:taskmanagement")
include(":feature:todayplan")
include(":feature:registration")
include(":feature:taskdetail")
include(":feature:plandetail")
include(":feature:planarrangement")
include(":feature:settings")
include(":feature:summaries")
include(":feature:analytics")
include(":summary")
