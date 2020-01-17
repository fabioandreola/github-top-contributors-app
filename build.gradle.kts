import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    groovy
}

group = "com.fabioandreola.github.topcontributors"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11
repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR1"
extra["spockVersion"] = "1.3-groovy-2.5"

dependencies {
    implementation(project(":github-top-contributors-api"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.cloud:spring-cloud-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.springframework.cloud:spring-cloud-starter-oauth2")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.2.26")



    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.spockframework:spock-core:${property("spockVersion")}")
    testImplementation("org.spockframework:spock-spring:${property("spockVersion")}")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock")
    testImplementation("io.rest-assured:rest-assured")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.bootRun {
    val properties: Map<String, Any> = System.getProperties().toMap() as Map<String, Any>
    systemProperties = properties
}
