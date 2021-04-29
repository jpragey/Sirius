package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sirius.backend.jvm.JvmScope.JvmLocalVariable;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Statement;

public class JvmMemberFunction {
	private AbstractFunction memberFunction;
	private boolean isStatic;
	private DescriptorFactory descriptorFactory;
	private Reporter reporter;
	private BackendOptions backendOptions;

	
	private ScopeManager scopeManager;

	public JvmMemberFunction(Reporter reporter, BackendOptions backendOptions, DescriptorFactory descriptorFactory, AbstractFunction memberFunction, boolean isStatic) {
		super();
		this.reporter = reporter;
		this.backendOptions = backendOptions;
		this.descriptorFactory = descriptorFactory;
		this.memberFunction = memberFunction;
		this.isStatic = isStatic;

		this.scopeManager = new ScopeManager(descriptorFactory);
		
	};
	
	
//	public void writeExpressionStatementBytecode(ClassWriter classWriter, MethodVisitor mv, ExpressionStatement statement, JvmScope scope) {
//		JvmExpression jvmExpression = new JvmExpression(reporter, descriptorFactory);
//		Expression expression = statement.getExpression();
//		jvmExpression.writeExpressionBytecode(mv, expression, scope);
//	}

	/** simulate "return ;"
	 *  
	 * @param classWriter
	 * @param mv
	 * @param statement
	 */
	public void writeDummyDefaultReturn(MethodVisitor mv) {	// TODO: refactor 'return' usage

		mv.visitInsn(RETURN);
	}

	private void writeFunctionContent(ClassWriter classWriter, MethodVisitor mv, List<FunctionFormalArgument> remainingParams) {
		JvmScope scope = scopeManager.enterNewScope(
				this.memberFunction.getQName().getLast() +
				"-" + remainingParams.size() + "-args"
				);
		
		if(remainingParams.isEmpty()) {
			// -- all params are manages, handle statements

			JvmStatementBlock bodyBlock = new JvmStatementBlock(reporter, descriptorFactory, scopeManager, memberFunction.getBodyStatements().get());
			bodyBlock.writeBytecode(classWriter, mv);

			writeDummyDefaultReturn(mv);
			
		} else {
			// -- manage first param and recurse
			FunctionFormalArgument param = remainingParams.remove(0);
			JvmLocalVariable varHolder = scope.addFunctionArgument(param);
			
			writeFunctionContent(classWriter, mv, remainingParams);
		}
		
		scope.markEnd();
		scopeManager.leaveScope();
	}

	/** write a java-callable "void main(String[] args) { functionName();}"
	 * 
	 * @param classWriter
	 * @param functionName
	 */
	private void writeJvmMainBytecode(ClassWriter classWriter, String functionName) {

		String functionDescriptor = "([Ljava/lang/String;)V";	// eg (Ljava/lang/String;)V
		int access = ACC_PUBLIC | ACC_STATIC;

		MethodVisitor mv = classWriter.visitMethod(access, "main" /*functionName*/, functionDescriptor,
				null /* String signature */,
				null /* String[] exceptions */);
		// -- call function (no arguments)
		if(memberFunction.getArguments().isEmpty()) {
			
			int invokeOpcode = INVOKESTATIC;
			String methodDescriptor = "()V";
			
			Optional<QName> parentQName = memberFunction
					.getQName()
					.parent();
			assert(parentQName.isPresent());
			String owner = parentQName.get()
					.child(Util.jvmPackageClassName /* "$package$"*/)
					.getStringElements().stream()
					
					.collect(Collectors.joining("/", "",""));
			mv.visitMethodInsn(
					invokeOpcode,		// opcode 
					owner,		// owner "java/io/PrintStream", 
					functionName, //"println", 
					methodDescriptor,			// "(Ljava/lang/String;)V",	// method descriptor 
					false 				// isInterface
					);

		} else {
			reporter.error("Error creating JVM main(){}: the function " + memberFunction.getQName().dotSeparated() + " has arguments (not supported yet).");
		}
		
		
		// -- 'main' return
		mv.visitInsn(RETURN);
//		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	/** Write bytecode for the whole function
	 * 
	 * @param classWriter
	 */
	public void writeBytecode(ClassWriter classWriter) {

		QName functionQName = memberFunction.getQName();
		
		String functionName = functionQName.getLast();
		Optional<List<Statement>> optBody = memberFunction.getBodyStatements();
		if(optBody.isEmpty()) {
			reporter.error("Can't generate bytecode for function " + memberFunction.getQName().dotSeparated() + ", body is missing."); // TODO add function location to message
		}

		String functionDescriptor = descriptorFactory.methodDescriptor(memberFunction);	// eg (Ljava/lang/String;)V
		int access = ACC_PUBLIC;
		if(isStatic)
			access |= ACC_STATIC;

		MethodVisitor mv = classWriter.visitMethod(access, functionName, functionDescriptor,
				null /* String signature */,
				null /* String[] exceptions */);

		JvmScope scope = scopeManager.enterNewScope(this.memberFunction.getQName().getLast());
		
		
		List<FunctionFormalArgument> currentArgs = new ArrayList<>(memberFunction.getArguments()); // TODO: shouldn't be mutable
		writeFunctionContent(classWriter, mv, currentArgs);

		scopeManager.leaveScope();
		scopeManager.writeLocalVariables(classWriter, mv);

		mv.visitMaxs(-1, -1);
		mv.visitEnd();

		// -- Opt. create a 'void main(java.lang.String[]' method
		backendOptions.getJvmMainFunctionQName().ifPresent(qname -> {
			if(functionQName.equals(qname)) {
				writeJvmMainBytecode(classWriter, qname.getLast());
				String jvmMainName = backendOptions.getJvmMainOption().get(); // function name, as defined in options
				backendOptions.markJvmMainAsWritten(jvmMainName);
			}
		});
	}
	
	@Override
	public String toString() {
		return memberFunction.getQName().dotSeparated() + 
				"(" + memberFunction.getArguments().size() + " params)";
	}
	
}