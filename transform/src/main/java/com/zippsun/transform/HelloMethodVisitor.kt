package com.zippsun.transform

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class HelloMethodVisitor(api: Int, methodVisitor: MethodVisitor?, access: Int, name: String?,
                         descriptor: String?
) : AdviceAdapter(
    api,
    methodVisitor,
    access,
    name,
    descriptor
) {
    override fun onMethodEnter() {
        super.onMethodEnter()
        //这里的mv是MethodVisitor
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Hello World!");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
