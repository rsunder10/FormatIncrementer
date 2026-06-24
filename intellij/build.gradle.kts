import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask

plugins {
    kotlin("jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.rsunder10"
version = "3.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaCommunity, "2024.2")
        instrumentationTools()
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation("junit:junit:4.13.2")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "242"
            untilBuild = provider { null }
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks {
    test {
        useJUnit()
    }

    withType<RunIdeTask>().configureEach {
        // Avoid MemorySizeConfigurator warnings in the plugin dev sandbox (no custom vmoptions file).
        jvmArgs("-Xmx2048m", "-XX:ReservedCodeCacheSize=512m")
        // The 2024.2 sandbox bundles an old JavaVersion parser that throws
        // "IllegalArgumentException: 25" when it downloads JetBrains' updated Gradle/JVM
        // compatibility matrix (which now lists Java 25). Disable that network update so
        // the sandbox uses its bundled, parseable matrix instead. (Registry keys can be
        // overridden via -D system properties.)
        jvmArgs("-Dgradle.compatibility.update.interval=0")
    }
}
