plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "1.9.20"
}

group = "com.github.BoBkiNN"

version = "1.0.0"

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        create("repo-secrets") {
            id = "com.github.BoBkiNN.RepoSecrets"
            implementationClass = "xyz.bobkinn.repoSecrets.gradle.RepoSecretsPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
}

