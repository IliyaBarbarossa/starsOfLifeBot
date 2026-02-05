plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

//	зависимость тг бота
	implementation ("org.telegram:telegrambots-springboot-longpolling-starter:9.+")
	implementation ("org.telegram:telegrambots-client:9.+")

//	зависимости базы
	implementation  ("org.postgresql:postgresql")
	implementation ("org.springframework.boot:spring-boot-starter-data-jpa")

	//	зависимости ii
	implementation ("org.json:json:20231013")


	compileOnly ("org.projectlombok:lombok:1.18.32")
	annotationProcessor ("org.projectlombok:lombok:1.18.32")


}

tasks.withType<Test> {
	useJUnitPlatform()
}
