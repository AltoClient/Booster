import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    id("java-library")
}

group = "me.jacobtread.mck.booster"
version = "1.0.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.17.0")
    implementation("org.apache.logging.log4j:log4j-api:2.17.0")
    implementation("io.netty:netty-all:4.1.72.Final")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

tasks.compileJava {
    sourceCompatibility = "16"
    targetCompatibility = "16"
}

