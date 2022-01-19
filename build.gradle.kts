
group = "com.jacobtread.mck.booster"
version = "1.0.0"

dependencies {
    implementation(project(":chat"))
    implementation(project(":utils"))
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("it.unimi.dsi:fastutil:8.5.6")
    implementation("io.netty:netty-all:4.1.72.Final")
}
