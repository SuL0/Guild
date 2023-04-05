group = "kr.sul.guild"
version = ext.get("version")!!

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.destroystokyo.paper:paper:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.2.0")
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.2.0")
    compileOnly("org.redisson:redisson:3.17.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation(project(":Common"))
}

val rootName = ext.get("rootName")!! as String
spigot {
    name = rootName
    authors = listOf("SuL")
    apiVersion = "1.12"
    version = project.version.toString()
    depends = listOf("ServerCore", "Citizens")
    softDepends = listOf("EnderVaults")
    commands {
        create("nbtview") {
            permission = "op.op"
            description = "아이템의 특정 NBT 값을 확인합니다."
        }
        create("돈")
        create("인사하기")
        create("이동지점알림끄기")
        create("방어구수정")
        create("customitems")
    }
}
