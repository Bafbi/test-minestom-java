plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.bafbi"
version = "0.1.0"

var fileName = "minestom-java.jar"

repositories {
    // ...
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    //...
    implementation("net.minestom:minestom-snapshots:96dbad809f")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.hibernate.orm:hibernate-core:7.0.0.Beta1")
    implementation("dev.hollowcube:polar:1.8.1")
    implementation("dev.hollowcube:schem:1.2.0")
}

tasks.withType<Jar> {
    manifest {
        // Change this to your main class
        attributes["Main-Class"] = "fr.bafbi.Main"
    }
}

tasks.register<Copy>("copyJar") {
    dependsOn("shadowJar") // make sure the JAR is built before copying it
    from(tasks.named("shadowJar"))
    into("server/") // specify the folder where you want to copy the JAR
    rename { fileName } // rename the JAR file
}

tasks.build {
    dependsOn("copyJar") // make sure the JAR is copied before building the project
}