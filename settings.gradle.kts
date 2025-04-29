rootProject.name = "developer-joyofenergy-java"

pluginManagement {
    val versions_version: String by settings
    val spring_boot_plugin_version: String by settings
    val spring_dependency_management_plugin_version: String by settings
    val spotless_version: String by settings
    plugins {
        id("io.spring.dependency-management") version spring_dependency_management_plugin_version
        id("org.springframework.boot") version spring_boot_plugin_version
        id("com.github.ben-manes.versions") version versions_version
        id("com.diffplug.spotless") version spotless_version
    }
}
