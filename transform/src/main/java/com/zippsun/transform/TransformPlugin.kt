package com.zippsun.transform

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class TransformPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("Hello CustomTransformPlugin")

        //新增的代码
        // 1、获取 Android 扩展
        val androidExtension = project.extensions.getByType(AppExtension::class.java)
        // 2、注册 Transform
        androidExtension.registerTransform(HelloTransform())

    }
}