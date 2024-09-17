package com.zippsun.transform

import com.zippsun.transform.base.BaseTransform
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File

class HelloTransform : BaseTransform() {
    override fun getClassVisitor(classWriter: ClassWriter): ClassVisitor {
        return HelloClassVisitor(classWriter)
    }

    override fun isNeedTraceClass(file: File): Boolean {
        val name = file.name
        if (!name.endsWith(".class")
            || name.startsWith("R.class")
            || name.startsWith("R$")
        ) {
            return false
        }
        return true
    }

    override fun getName(): String {
        return "HelloTransform"
    }
}