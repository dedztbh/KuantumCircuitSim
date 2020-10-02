import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}
group = "com.dedztbh"
version = "1.3.4"

val ejmlVersion = "0.39"

repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://jitpack.io")
}

kotlin {
    sourceSets {
        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
        }
    }
}

dependencies {
    implementation("org.ejml:ejml-core:${ejmlVersion}")
    implementation("org.ejml:ejml-zdense:${ejmlVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("com.github.cvb941:kotlin-parallel-operations:1.3")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.11.1")
    implementation("org.slf4j:slf4j-nop:1.7.30")
    testImplementation("org.ejml:ejml-cdense:${ejmlVersion}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xno-param-assertions",
        "-Xno-call-assertions",
        "-Xno-receiver-assertions"
    )
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("kuantum_circuit_sim")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "MainKt"))
        }
        minimize()
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}