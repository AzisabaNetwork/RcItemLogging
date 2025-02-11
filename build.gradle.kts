plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.shadow)
}

version = System.getenv("VERSION") ?: "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly(libs.paper.api)
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = name
            version = version
            artifact(tasks.jar)
        }
    }

    repositories {
        maven {
            name = "azisaba-repo"
            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
            url = if(project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.azisaba.net/repository/maven-snapshots/")
            } else {
                uri("https://repo.azisaba.net/repository/maven-releases/")
            }
        }
    }
}