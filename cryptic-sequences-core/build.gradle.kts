plugins {
  kotlin("multiplatform")
  id("maven-publish")
  id("signing")
}

kotlin {

  jvm()

  linuxX64()
  mingwX86()
  mingwX64()
  macosX64()

  sourceSets {
    @Suppress("UNUSED_VARIABLE")
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
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
  }

  sourceSets.all {
    kotlin.setSrcDirs(listOf("$name/src"))
    resources.setSrcDirs(listOf("$name/resources"))
    languageSettings.apply {
      useExperimentalAnnotation("kotlin.Experimental")
    }
  }

  tasks.named<Test>("jvmTest") {
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

  signing {
    useGpgCmd()
    sign(publishing.publications["jvm"])
  }

  publishing {

    publications {

      withType<MavenPublication>().configureEach {
        groupId = properties["groupId"]!!.toString()
        version = properties["version"]!!.toString()
      }

      named<MavenPublication>("jvm") {
        pom {
          name.set("cryptic-sequences core library")
          description.set("Create small cyphers to generate random-looking unique ids")
          url.set("https://github.com/plcarmel/cryptic-sequences")
          licenses {
            license {
              name.set("MIT")
              url.set("https://mit-license.org/")
              distribution.set("repo")
            }
          }
          scm {
            url.set("https://github.com/plcarmel/cryptic-sequences")
            connection.set("scm:git:git@github.com:plcarmel/cryptic-sequences.git")
            developerConnection.set("scm:git:ssh:git@github.com:plcarmel/cryptic-sequences.git")
          }
        }
      }

    }

    repositories {
      maven {
        name = "Staging"
        url = uri("$buildDir/repos/staging")
      }
    }

  }
}

