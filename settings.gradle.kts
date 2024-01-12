pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.toString().startsWith("com.github.")) {
                val parts = requested.id.toString().split(".", limit = 4)
                useModule("com.github:${parts[1]}:${parts[2]}")
            }
        }
    }
}





rootProject.name = "Roomies"
include(":app")
 