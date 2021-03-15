plugins {
  kotlin("jvm")
}

group = "net.plcarmel.cryptic-sequences"
version = "2.2.4-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
  implementation(
    "org.jetbrains.kotlin",
    "kotlin-stdlib",
    "1.4.31",
    classifier="modular"
  )
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
  testImplementation("org.mockito:mockito-junit-jupiter:3.8.0")
}

tasks {
  compileJava {
    dependsOn(compileKotlin)
    options.compilerArgs = listOf(
      "--patch-module",
      "net.plcarmel.crypticsequences.core=${sourceSets.main.get().output.asPath}"
    )
  }

}
