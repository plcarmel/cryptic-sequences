import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("multiplatform")
  id("maven-publish")
  id("signing")
  id("com.github.johnrengelman.shadow") version("6.1.0")
}

kotlin {

  jvm()
  val linux64Target = linuxX64()
  val windows32Target = mingwX86()
  val windows64Target = mingwX64()
  val macOsTarget = macosX64()

  linuxX64 {
    val main by compilations.getting
    val interop by main.cinterops.creating
    interop.defFile = File("${project.projectDir}/nativeInterop/src/cinterop/interop.def")
  }

  val partiallySupportedTargets =
    listOf(
      windows32Target,
      windows64Target,
      macOsTarget
    )

  val supportedPosixTargets =
    listOf(
      linux64Target
    )

  partiallySupportedTargets.forEach {
    it.apply {
      binaries {
        executable {
          entryPoint = "net.plcarmel.crypticsequences.cli.mainWithBasicIo"
        }
      }
    }
  }

  supportedPosixTargets.forEach {
    it.apply {
      binaries {
        executable {
          entryPoint = "net.plcarmel.crypticsequences.cli.main"
        }
      }
    }
  }

  sourceSets {
    @Suppress("UNUSED_VARIABLE")
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        implementation(project(":cryptic-sequences-core"))
        implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
      }
    }
    @Suppress("UNUSED_VARIABLE")
    val jvmMain by getting {
      dependencies {
        implementation(kotlin("stdlib-jdk8"))
      }
    }
    @Suppress("UNUSED_VARIABLE")
    val jvmTest by getting {
      val jupiterVersion = "5.7.1"
      val mockitoVersion = "3.8.0"
      dependencies {
        implementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
        implementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
        implementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
        runtimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
      }
    }
    @Suppress("UNUSED_VARIABLE")
    val linuxX64Main by getting {
      dependencies {
        implementation(project(":cryptic-sequences-core"))
      }
    }
  }

  sourceSets.all {
    kotlin.setSrcDirs(listOf("$name/src"))
    resources.setSrcDirs(listOf("$name/resources"))
  }

  repositories {
    mavenCentral()
    maven {
      url = uri("https://kotlin.bintray.com/kotlinx")
    }
  }

  tasks {

    val shadowJar = task<ShadowJar>("shadowJar")

    named("build") {
      dependsOn(shadowJar)
    }

    shadowJar.apply {
      val jvmJar = named<org.gradle.jvm.tasks.Jar>("jvmJar").get()
      from(jvmJar.archiveFile)
      archiveClassifier.set("shadow")
      configurations.add(project.configurations.named("jvmRuntimeClasspath").get())
      manifest {
        attributes("Main-Class" to "net.plcarmel.crypticsequences.cli.JavaMainKt")
      }
    }

    named<Test>("jvmTest") {
      useJUnitPlatform()
      testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
          org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
          org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      }
    }
  }

  publishing {
    publications {
      named<MavenPublication>("jvm") {
        artifact(tasks.named("shadowJar"))
      }
    }
  }
}

