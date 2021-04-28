package org.sirius.backend.jvm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public interface JvmStatement {
	public void writeBytecode(ClassWriter classWriter, MethodVisitor mv);

}