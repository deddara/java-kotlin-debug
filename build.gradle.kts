import com.google.protobuf.gradle.id

plugins {
  id("com.diffplug.spotless") version "6.25.0"
  id("com.google.protobuf") version "0.9.4"
  id("idea")
  id("io.spring.dependency-management") version "1.1.4"
  id("jacoco")
  id("java")
  id("org.springframework.boot") version "3.2.2"
}

group = "money.vivid"

version = "dev"

repositories { mavenCentral() }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

val grpcMockVersion = "0.11.0"
val grpcSpringBootStarterVersion = "3.0.0.RELEASE"
val grpcVersion = "1.61.1"
val protocVersion = "3.25.3"
val testcontainersVersion = "1.19.6"

dependencies {
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  compileOnly("org.projectlombok:lombok")
  compileOnly("javax.annotation:javax.annotation-api:1.3.2") // for grpc, see grpc/grpc-java#9179

  implementation("net.devh:grpc-client-spring-boot-starter:$grpcSpringBootStarterVersion")
  implementation("net.devh:grpc-server-spring-boot-starter:$grpcSpringBootStarterVersion")
  implementation("org.flywaydb:flyway-core")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.kafka:spring-kafka")

  runtimeOnly("com.h2database:h2")

  testAnnotationProcessor("org.projectlombok:lombok")
  testCompileOnly("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.kafka:spring-kafka-test")
  testImplementation("org.testcontainers:redpanda")
}

dependencyManagement {
  imports {
    mavenBom("com.google.protobuf:protobuf-bom:$protocVersion")
    mavenBom("io.grpc:grpc-bom:$grpcVersion")
    mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
  }
}

sourceSets { main { proto { srcDir("src/main/resources/proto") } } }

protobuf {
  protoc { artifact = "com.google.protobuf:protoc:$protocVersion" }
  plugins { id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" } }
  generateProtoTasks { all().forEach { it.plugins { id("grpc") } } }
}

spotless {
  java { googleJavaFormat() }
  kotlinGradle { ktfmt() }
  format("misc") {
    target("*.md", ".gitignore")
    indentWithSpaces(2)
    trimTrailingWhitespace()
    endWithNewline()
  }
  json {
    target("**/*.json")
    gson().sortByKeys().indentWithSpaces(2)
  }
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports { xml.required.set(true) }
}

tasks.withType<Test> { useJUnitPlatform() }
