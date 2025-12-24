plugins {
	java
	eclipse
	id("org.springframework.boot") version "3.5.9"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.thisjowi"
version = "0.0.1-SNAPSHOT"
description = "Authentication service"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springBootAdminVersion"] = "3.5.6"
extra["springCloudVersion"] = "2025.0.1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("de.codecentric:spring-boot-admin-starter-client")
	implementation("org.springframework.cloud:spring-cloud-starter")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc")
	implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
