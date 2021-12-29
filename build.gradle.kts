import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"
}

group = "com.pins"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
//	implementation ("org.springframework.data:spring-data-neo4j:5.3.2.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation ("com.graphql-java-kickstart:graphql-spring-boot-starter:11.0.0")
	runtimeOnly ("com.graphql-java-kickstart:playground-spring-boot-starter:11.0.0")
	runtimeOnly ("com.graphql-java-kickstart:voyager-spring-boot-starter:11.0.0")

	implementation ("org.jobrunr:jobrunr-spring-boot-starter:4.0.5")

	implementation ("org.springframework.boot:spring-boot-starter-security")
	testImplementation ("org.springframework.security:spring-security-test")

	implementation ("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.2")
	// Uncomment the next line if you want to use RSASSA-PSS (PS256, PS384, PS512) algorithms:
	//runtimeOnly("org.bouncycastle:bcprov-jdk15on:1.60")
	runtimeOnly("io.jsonwebtoken:jjwt-gson:0.11.2") // or 'io.jsonwebtoken:jjwt-jakson:0.11.2' for gson

	implementation ("com.google.api-client:google-api-client:1.31.5")

	// compile group: 'com.google.http-client', name: 'google-http-client-jackson2', version: '1.22.0'
	implementation ("com.google.http-client:google-http-client-jackson2:1.22.0")

//	neo4j dependancy

	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.testcontainers:junit-jupiter:1.15.3")
	testImplementation("org.testcontainers:neo4j:1.15.3")

	implementation("org.springframework:spring-webflux:5.3.14")

	implementation("jmagick:jmagick:6.6.9")
	implementation("org.im4java:im4java:1.4.0")
	implementation("commons-io:commons-io:2.11.0")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
