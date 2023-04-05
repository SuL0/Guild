group = "kr.sul.guild"
version = ext.get("version")!!

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("io.github.waterfallmc:waterfall-api:1.18-R0.1-SNAPSHOT")
    compileOnly("org.redisson:redisson:3.17.6")
    compileOnly("io.github.revxrsal:bungee:3.0.8")
    compileOnly("io.github.revxrsal:common:3.0.8")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
    implementation(project(":Common"))
}