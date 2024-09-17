import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}


dependencies {
    implementation("com.android.tools.build:gradle:4.1.3")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
    implementation("org.ow2.asm:asm-util:9.6")


}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}
gradlePlugin {
    plugins {
        create("zippPlugin") {
            id = "com.zippsun.zippPlugin"   // 对应 module plugin 的ID
            implementationClass = "com.zippsun.transform.TransformPlugin"
        }
    }

}
publishing {
    repositories {
        maven {
            url = uri("../repo")
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "com.zippsun.transform" // 替换为你自己的 Group ID
            artifactId = "plugin" // 替换为你自己的 Artifact ID
            version = "1.0.0" // 替换为你自己的版本号

            from(components["java"]) // 发布 Java 组件
        }
    }

    repositories {
        mavenLocal() // 发布到本地 Maven 仓库
    }
}
