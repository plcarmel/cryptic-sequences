import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("multiplatform")
  id("com.github.johnrengelman.shadow") version("6.1.0")
}

kotlin {

  jvm()
  val linux64Target = linuxX64()
  val windows32Target = mingwX86()
  val windows64Target = mingwX64()
  val macOsTarget = macosX64()

  val partiallySupportedTargets =
    listOf(
      windows32Target,
      windows64Target,
      macOsTarget
    )

  val posixTargets =
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

  posixTargets.forEach {
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

  task<ShadowJar>(name="shadowJar") {
    val jvmJar = tasks.withType<org.gradle.jvm.tasks.Jar>().named("jvmJar").get()
    dependsOn(jvmJar)
    from(jvmJar.archiveFile)
    archiveClassifier.set("shadow")
    configurations.add(project.configurations.named("jvmRuntimeClasspath").get())
    manifest {
      attributes("Main-Class" to "net.plcarmel.crypticsequences.cli.JavaMainKt")
    }
  }

}
