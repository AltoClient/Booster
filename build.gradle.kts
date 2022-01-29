
group = "com.jacobtread.alto.booster"
version = "1.0.0"

dependencies {
    implementation(project(":chat"))
    implementation(project(":utils"))
    implementation(project(":logger"))
    implementation(project(":authlib"))
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("it.unimi.dsi:fastutil:8.5.6")
    implementation("io.netty:netty-all:4.1.72.Final")
    implementation("org.joml:joml:1.10.3")
}
