import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("multiplatform")
  id("com.github.johnrengelman.shadow") version("6.1.0")
}

kotlin {

  jvm()

  val nativeTarget = linuxX64()

  nativeTarget.apply {
    binaries {
      executable {
        entryPoint = "net.plcarmel.crypticsequences.cli.mainWithBasicIo"
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
    val linuxX64Main by getting {
      dependencies {
        implementation(kotlin("stdlib-jdk8"))
      }
    }
  }

  sourceSets.all {
    kotlin.setSrcDirs(listOf("$name/src"))
    resources.setSrcDirs(listOf("$name/resources"))
    languageSettings.apply {
      useExperimentalAnnotation("kotlin.Experimental")
      useExperimentalAnnotation("kotlinx.cli.ExperimentalCli")
    }
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
      attributes("Main-Class" to "net.plcarmel.crypticsequences.cli.MainKt")
    }
  }

}
