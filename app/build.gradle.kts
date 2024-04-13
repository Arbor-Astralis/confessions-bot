plugins {
    alias(libs.plugins.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    implementation(libs.guava)
    implementation("dev.kord:kord-core:0.13.1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(19)
    }
}

application {
    mainClass = "arbor.astralis.confessions.MainKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
