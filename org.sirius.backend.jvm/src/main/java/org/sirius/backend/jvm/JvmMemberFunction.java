package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.backend.jvm.JvmScope.LocalVarHolder;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;

public class JvmMemberFunction {
	private AbstractFunction memberFunction;
	private boolean isStatic;
	private DescriptorFactory descriptorFactory;
	private Reporter reporter;
	private BackendOptions backendOptions;

	
	private static class ScopeManager {
		private JvmScope currentScope ;
		private DescriptorFactory descriptorFactory;
		private JvmScope rootScope;
		
		public ScopeManager(DescriptorFactory descriptorFactory) {
			this.descriptorFactory = descriptorFactory;
			this.rootScope = new JvmScope(descriptorFactory, Optional.empty(), "<root>");
			this.currentScope = this.rootScope;
		}
		
//		public JvmScope getCurrent() {
//			return this.currentScope;
//		}

		@Override
		public String toString() {
			return currentScope.toString();
		}
		public JvmScope enterNewScope(String dbgName) {
			JvmScope current = this.currentScope;
			assert(current != null);
			JvmScope scope = new JvmScope(descriptorFactory, Optional.of(current),  dbgName);
			current.addSubScopes(scope);
			this.currentScope = scope;
			return scope;
		}
		public void leaveScope() {
			this.currentScope = this.currentScope.getParentScope().get();
		}

		public void writeLocalVariables(ClassWriter classWriter, MethodVisitor mv) {
			rootScope.indexedScope(descriptorFactory).writeLocalVariableStatements(classWriter, mv);
		}
	}
	
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

	public void writeReturnStatementBytecode(ClassWriter classWriter/*, MemberFunction declaration*/, MethodVisitor mv, ReturnStatement statement, JvmScope scope) {

		// -- write return expression
		JvmExpression expr = new JvmExpression(reporter, descriptorFactory);
		expr.writeExpressionBytecode(mv, statement.getExpression(), scope);

		// -- write return
		org.sirius.frontend.api.Type type = statement.getExpressionType();

		if(type instanceof IntegerType) {
			mv.visitInsn(ARETURN);	// TODO ???
//						    mv.visitInsn(IRETURN);
		} else if(type instanceof ClassType) {
			mv.visitInsn(ARETURN);
		} else {
			reporter.error("Currently unsupported expression type in return statement: " + type);
		}
	}

	public void writeIfElseStatementBytecode(ClassWriter classWriter, MethodVisitor mv, IfElseStatement statement, JvmScope scope) {
		/*
		 * if-then :
		 *   evaluate expr
		 *   IFEQ goto endifLabel
		 *   ifBlock
		 * endifLabel:
		 * 
		 * if-then-else :
		 *   evaluate expr
		 *   IFEQ goto endifLabel
		 *   <ifBlock>
		 * endifLabel:
		 * 	 GOTO endElseBlock
		 *   <elseBlock>
		 * endElseBlock
		 */


		Expression expr = statement.getExpression();
		Statement ifStmt = statement.getIfStatement();
		Optional<Statement> elseStmt = statement.getElseStatement();

		new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, expr, scope);
		// -- IFEQ

		Label endifLabel = new Label();
		mv.visitJumpInsn(Opcodes.IFEQ, endifLabel);

		JvmStatementBlock ifBlock = new JvmStatementBlock(Arrays.asList(ifStmt));
		ifBlock.writeByteCode(classWriter, mv);


		if(elseStmt.isEmpty()) {
			mv.visitLabel(endifLabel);
		} else {
			Label endElseLabel = new Label();
			mv.visitJumpInsn(Opcodes.GOTO, endElseLabel);

			mv.visitLabel(endifLabel);

			JvmStatementBlock elseBlock = new JvmStatementBlock(Arrays.asList(elseStmt.get()));
			elseBlock.writeByteCode(classWriter, mv);

			mv.visitLabel(endElseLabel);
		}
	}

	public void writeExpressionStatementBytecode(ClassWriter classWriter, MethodVisitor mv, ExpressionStatement statement, JvmScope scope) {
		JvmExpression jvmExpression = new JvmExpression(reporter, descriptorFactory);
		Expression expression = statement.getExpression();
		jvmExpression.writeExpressionBytecode(mv, expression, scope);
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

	public class JvmStatementBlock {
		List<Statement> statements;

		public JvmStatementBlock(List<Statement> statements) {
			super();
			this.statements = statements;
		}

		// -- Write var init code at the start of function/block bytecode
		private void writeLocalVarsInitCode(JvmScope.LocalVarHolder h, MethodVisitor mv, JvmScope scope) {
//			LocalVariableStatement st = h.getStatement();
			int locvarIndex = h.getIndex();
			Optional<Expression> optInitExp = h.getInitExp();
			if(optInitExp.isPresent()) {
				Expression initExpr = optInitExp.get();
				JvmExpression jvmExpr = new JvmExpression(reporter, descriptorFactory);
				jvmExpr.writeExpressionBytecode(mv, initExpr, scope);

				mv.visitVarInsn(Opcodes.ASTORE, locvarIndex);

			}
		}

		public void writeByteCode(ClassWriter classWriter, MethodVisitor mv) {

			JvmScope scope = scopeManager.enterNewScope("{block}");


			// Collect local variables to generate initialization code
			for(Statement st: statements ) {
				if(st instanceof LocalVariableStatement) { 
					LocalVariableStatement locVarsStmt = (LocalVariableStatement)st;
					scope.addLocalVariable(locVarsStmt);
				}
			}
			// Write local var init code
			for(JvmScope.LocalVarHolder h: scope.getLocVarsStmts()) {
				writeLocalVarsInitCode(h, mv, scope);
			}

			for(Statement st: statements ) {
				if(st instanceof ReturnStatement) {
					writeReturnStatementBytecode(classWriter, mv, (ReturnStatement)st, scope);
				} else if(st instanceof IfElseStatement) {
					writeIfElseStatementBytecode(classWriter, mv, (IfElseStatement)st, scope);
				} else if(st instanceof ExpressionStatement) {
					writeExpressionStatementBytecode(classWriter, mv, (ExpressionStatement)st, scope);
//					writeIfElseStatementBytecode(classWriter, mv, (IfElseStatement)st, scope);
				} else if(st instanceof LocalVariableStatement) { 
					// Ignore
				} else {
					throw new UnsupportedOperationException("No bytecode to write for statement " + st.getClass().getCanonicalName());
				}
			}

			scope.markEnd();
			scopeManager.leaveScope();
			
//			scope.writeLocalVariableStatements(classWriter, mv);
		}
	}

	private void writeFunctionContent(ClassWriter classWriter, MethodVisitor mv, List<FunctionFormalArgument> remainingParams) {
		JvmScope scope = scopeManager.enterNewScope(
				this.memberFunction.getQName().getLast() +
				"-" + remainingParams.size() + "-args"
				);
		
		if(remainingParams.isEmpty()) {
			// -- all params are manages, handle statements

			JvmStatementBlock bodyBlock = new JvmStatementBlock(memberFunction.getBodyStatements().get());
			bodyBlock.writeByteCode(classWriter, mv);

			writeDummyDefaultReturn(mv);
			
		} else {
			// -- manage first param and recurse
			FunctionFormalArgument param = remainingParams.remove(0);
			LocalVarHolder varHolder = scope.addFunctionArgument(param);
			
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
//		System.out.println("Writing JVM main() bytecode for function: " + functionName);

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
//		System.out.println("Trying to create JVM entry point at " + functionQName);
//		System.out.println(" From options: " + backendOptions.getJvmMainFunctionQName());
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