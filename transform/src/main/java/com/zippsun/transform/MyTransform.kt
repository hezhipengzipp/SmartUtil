package com.zippsun.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager

import com.android.utils.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarFile

class MyTransform : Transform() {
    override fun getName(): String {
        return "ZippsunTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        printLog()
        transformInvocation?.inputs?.forEach {
            // 一、输入源为文件夹类型
            it.directoryInputs.forEach { directoryInput ->
                //1、TODO 针对文件夹进行字节码操作，这个地方我们就可以做一些狸猫换太子，偷天换日的事情了
                //先对字节码进行修改，在复制给 dest
                //2、构建输出路径 dest
                val dest = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                //3、将文件夹复制给 dest ，dest 将会传递给下一个 Transform
                traceFile(directoryInput.file, dest)
//                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            // 二、输入源为 jar 包类型
            it.jarInputs.forEach { jarInput ->
                // 1. 创建临时目录用于解压 jar 包
                val tempDir = File(jarInput.file.parentFile, "${jarInput.name}_temp")
                tempDir.mkdirs()

                // 2. 解压 jar 包到临时目录
                val jar = JarFile(jarInput.file)
                val enumeration = jar.entries()
                while (enumeration.hasMoreElements()) {
                    val entry = enumeration.nextElement()
                    val file = File(tempDir, entry.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        val inputStream = jar.getInputStream(entry)
                        val outputStream = FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        inputStream.close()
                        outputStream.close()
                    }
                }
                jar.close()

                // 3. 对解压后的临时目录进行插桩处理
                val dest = transformInvocation.outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                traceFile(tempDir, dest)

                // 4. 删除临时目录
                tempDir.deleteRecursively()
            }
        }
    }

    fun traceFile(inputFile: File, outputFile: File) {
        if (inputFile.isDirectory) {
            // 如果是目录，递归处理子文件夹和 jar 包
            inputFile.listFiles()?.forEach { childFile ->
                val childOutputFile = File(outputFile, childFile.name)
                if (childFile.isDirectory) {
                    traceFile(childFile, childOutputFile) // 处理子文件夹
                } else if (childFile.name.endsWith(".jar")) {
                    traceJarFile(childFile, childOutputFile) // 处理 jar 包
                }
            }
        }  else if (inputFile.isFile && inputFile.name.endsWith(".class")) {
            // 如果是 class 文件，进行字节码操作
            System.out.println("inputFile: " + inputFile.absolutePath)
            val inputStream = FileInputStream(inputFile)
            val outputStream = FileOutputStream(outputFile)

            val classReader = ClassReader(inputStream)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            classReader.accept(HelloClassVisitor(classWriter), ClassReader.SKIP_DEBUG)
            outputStream.write(classWriter.toByteArray())

            inputStream.close()
            outputStream.close()
        } else {
            // 忽略其他类型的文件
            println("Ignoring file: ${inputFile.absolutePath}")
        }
    }
    fun traceJarFile(jarFile: File, outputDir: File) {
        // 解压 jar 包
        val jar = JarFile(jarFile)
        val enumeration = jar.entries()
        while (enumeration.hasMoreElements()) {
            val entry = enumeration.nextElement()
            if (entry.name.endsWith(".class")) {
                // 处理 class 文件
                val classFile = File(outputDir, entry.name)
                classFile.parentFile.mkdirs() // 创建父目录
                val inputStream = jar.getInputStream(entry)
                val outputStream = FileOutputStream(classFile)

                val classReader = ClassReader(inputStream)
                val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
                classReader.accept(HelloClassVisitor(classWriter), ClassReader.SKIP_DEBUG)
                outputStream.write(classWriter.toByteArray())

                inputStream.close()
                outputStream.close()
            }
        }
        jar.close()
    }
    /**
     * 打印一段 log 日志
     */
    fun printLog() {
        println()
        println("******************************************************************************")
        println("******                                                                  ******")
        println("******                欢迎使用 ZippsunTransform 编译插件                    ******")
        println("******                                                                  ******")
        println("******************************************************************************")
        println()
    }
}