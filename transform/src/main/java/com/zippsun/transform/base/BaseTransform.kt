package com.zippsun.transform.base

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

abstract class BaseTransform : Transform() {
    val mWaitableExecutor = WaitableExecutor.useNewFixedSizeThreadPool(4)
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        // 需要处理的数据类型,这里表示class文件
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        // 作用范围
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        printCopyRight()
        // TransformOutputProvider管理输出路径,如果消费型输入为空,则outputProvider也为空
        val outputProvider = transformInvocation?.outputProvider

        //当前是否是增量编译,由isIncremental方法决定的
        // 当上面的isIncremental()写的返回true,这里得到的值不一定是true,还得看当时环境.比如clean之后第一次运行肯定就不是增量编译嘛.
        val incremental = transformInvocation?.isIncremental
        if (incremental == false) {
            // 非增量编译,删除所有的内容
            outputProvider?.deleteAll()
        }

        //transformInvocation.inputs的类型是Collection<TransformInput>,可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        transformInvocation?.inputs?.forEach { input ->
            // 处理jar包
            input.jarInputs.forEach { jarInput ->
                mWaitableExecutor.execute {
                    processJarInput(jarInput, outputProvider!!, incremental!!)
                }
            }
            // 处理源文件
            input.directoryInputs.forEach { directoryInput ->
                mWaitableExecutor.execute {
                    processDirectoryInput(directoryInput, outputProvider!!, incremental!!)
                }
            }
        }

    }

    private fun transformDirectory(directoryInput: DirectoryInput, dest: File?) {
        val extensions = arrayOf("class")
        val fileList = FileUtils.listFiles(directoryInput.file, extensions, true)
        val outputFilePath = dest?.absolutePath
        val inputFilePath = directoryInput.file.absolutePath
        println("outputFilePath: $outputFilePath")

        fileList.forEach { inputFile ->
            println("替换前  file.absolutePath = ${inputFile.absolutePath}")
            val outputFullPath = inputFile.absolutePath.replace(inputFilePath, outputFilePath!!)
            println("替换后  file.absolutePath = ${outputFullPath}")
            val outputFile = File(outputFullPath)
            //创建文件
            FileUtils.touch(outputFile)
            //单个单个地复制文件
//            FileUtils.copyFile(file, outputFile)
            transformSingleFile(inputFile, outputFile)
        }
    }

    /**
     * 处理源码文件
     * 将修改过的字节码copy到dest,就可以实现编译期间干预字节码的目的
     */
    fun processDirectoryInput(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
        val dest = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        FileUtils.forceMkdir(dest)
        println("isIncremental: $isIncremental")
        if (isIncremental) {


            val srcDirPath = directoryInput.file.absolutePath
            val destDirPath = dest.absolutePath

            val fileStatusMap = HashMap(directoryInput.changedFiles)
            fileStatusMap.forEach { (inputFile, status) ->
                val destFilePath = inputFile.absolutePath.replace(srcDirPath, destDirPath)
                val destFile = File(destFilePath)
                when (status) {
                    Status.NOTCHANGED -> {}
                    Status.ADDED, Status.CHANGED -> {
                        FileUtils.touch(destFile)
                        transformSingleFile(inputFile, destFile)
                    }
                    Status.REMOVED -> {
                        if (destFile.exists()) {
                            FileUtils.forceDelete(destFile)
                        }
                    }
                }}
//            val fileStatusMap = directoryInput.changedFiles
//            val iterator = fileStatusMap.iterator()
//            while (iterator.hasNext()) {
//                val changedFile = iterator.next()
//                val status = changedFile.value
//                val inputFile = changedFile.key
//                val destFilePath = inputFile.absolutePath.replace(
//                    srcDirPath,
//                    destDirPath
//                )
//                val destFile = File(destFilePath)
//                when (status) {
//                    Status.NOTCHANGED -> {}
//                    Status.ADDED, Status.CHANGED -> {
//                        FileUtils.touch(destFile)
//                        transformSingleFile(inputFile, destFile)
//                    }
//
//                    Status.REMOVED -> {
//                        if (destFile.exists()) {
//                            FileUtils.forceDelete(destFile)
//                            iterator.remove() // 安全删除
//                        }
//                    }
//                }
//            }
        } else {
            transformDirectory(directoryInput, dest)
        }
    }

    fun transformSingleFile(inputFile: File?, destFile: File) {
        println("拷贝单个文件")
        traceFile(inputFile, destFile)
    }

    fun traceFile(inputFile: File?, destFile: File) {
        synchronized(this) {
            if (isNeedTraceClass(inputFile!!)) {
                println("${inputFile.name} ---- 需要插桩 ----")
                val fileInputStream = FileInputStream(inputFile)
                val fileOutputStream = FileOutputStream(destFile)

                val classReader = ClassReader(fileInputStream)
                val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
                classReader.accept(getClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)

                fileOutputStream.write(classWriter.toByteArray())
                fileInputStream.close()
                fileOutputStream.close()
            } else {
                FileUtils.copyFile(inputFile, destFile)
            }
        }
    }

    abstract fun getClassVisitor(classWriter: ClassWriter): ClassVisitor

    fun processJarInput(
        jarInput: JarInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
        val status = jarInput.status
        val dest = outputProvider.getContentLocation(
            jarInput.file.absolutePath,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        if (isIncremental) {
            when (status) {
                Status.NOTCHANGED -> {}
                Status.ADDED, Status.CHANGED -> {
                    transformJar(jarInput.file, dest)
                }

                Status.REMOVED -> {
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                }
            }
        }
    }

    fun transformJar(jarInput: File, dest: File) {
        FileUtils.copyFile(jarInput, dest)
    }

    abstract fun isNeedTraceClass(file: File): Boolean

    /**
     * 加个打印日志,表示执行到当前Transform了,有标志性,很容易看到
     */
    fun printCopyRight() {
        println()
        println("******************************************************************************")
        println("******                                                                  ******")
        println("******                欢迎使用 Transform 插件                 ******")
        println("******                                                                  ******")
        println("******************************************************************************")
        println()
    }
}