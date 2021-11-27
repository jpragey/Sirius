package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sirius.backend.jvm.JvmScope.JvmLocalVariable;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public record ClassExecutorFunction(QName classQName, List<Statement> body, Type returnType) {

	public static String functionSName = Constants.SIRIUS_EXECCLASS_EXEC_FUNC_NAME;
	
	public void writeBytecode(ClassWriter classWriter, Reporter reporter, DescriptorFactory descriptorFactory) {
		boolean isStatic = false; // TODO ???
		List<FunctionParameter> currentArgs = List.of();
		String functionDescriptor = descriptorFactory.methodDescriptor(returnType, currentArgs /* TODO: args*/);	// eg (Ljava/lang/String;)V
		int access = ACC_PUBLIC;
		if(isStatic)
			access |= ACC_STATIC;

		String signature = null;
		String[] exceptions = null;
		MethodVisitor mv = classWriter.visitMethod(access, functionSName, functionDescriptor,
				signature,
				exceptions);

		ScopeManager scopeManager = new ScopeManager(descriptorFactory); /* TODO: parent scope ??? */
		JvmScope scope = scopeManager.enterNewScope(functionSName);
		
		writeFunctionContent(classWriter, mv, currentArgs, scopeManager, reporter, descriptorFactory);

		scopeManager.leaveScope();
		scopeManager.writeLocalVariables(classWriter, mv);

		mv.visitMaxs(-1, -1);
		mv.visitEnd();
	}
	
	private void writeFunctionContent(ClassWriter classWriter, MethodVisitor mv, List<FunctionParameter> remainingParams,
			ScopeManager scopeManager, Reporter reporter, DescriptorFactory descriptorFactory) {

		QName functionQName = classQName.child(functionSName);
		JvmScope scope = scopeManager.enterNewScope(
				functionQName.getLast() +
				"-" + remainingParams.size() + "-args"
				);
		
		if(remainingParams.isEmpty()) {
			// -- all params are manages, handle statements

			JvmStatementBlock bodyBlock = new JvmStatementBlock(reporter, descriptorFactory, scopeManager, body);
			bodyBlock.writeBytecode(classWriter, mv);

			writeDummyDefaultReturn(mv);
			
		} else {
			// -- manage first param and recurse
			FunctionParameter param = remainingParams.remove(0);
			JvmLocalVariable varHolder = scope.addFunctionArgument(param);
			
			writeFunctionContent(classWriter, mv, remainingParams, scopeManager, reporter, descriptorFactory);
		}
		
		scope.markEnd();
		scopeManager.leaveScope();
	}
	
	/** simulate "return ;"
	 *  
	 * @param classWriter
	 * @param mv
	 * @param statement
	 */
	public void writeDummyDefaultReturn(MethodVisitor mv) {	// TODO: refactor 'return' usage

		mv.visitInsn(RETURN);
	}
	
}
