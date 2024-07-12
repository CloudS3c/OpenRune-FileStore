dependencies {
    implementation(project(":filestore"))
    implementation("io.netty:netty-buffer:4.1.107.Final")
    implementation("dev.openrune:js5server:1.0.6")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("cc.ekblad:4koma:1.1.0")
    implementation("me.tongfei:progressbar:0.9.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("commons-io:commons-io:2.15.1")
    implementation("com.displee:rs-cache-library:7.1.0")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            url = uri("K:/rsprot/repo")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}