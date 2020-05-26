package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public class JvmMemberFunction {
		private AbstractFunction memberFunction;
		private boolean isStatic;
		private DescriptorFactory descriptorFactory;
		private Reporter reporter;

		private Stack<JvmScope> scopes = new Stack<>();
		
//		private final static int locvarIndex = 0;	// TODO: remove
		
		public JvmMemberFunction(Reporter reporter, DescriptorFactory descriptorFactory, AbstractFunction memberFunction, boolean isStatic) {
			super();
			this.reporter = reporter;
			this.descriptorFactory = descriptorFactory;
			this.memberFunction = memberFunction;
			this.isStatic = isStatic;
			
			this.scopes.push(new JvmScope(descriptorFactory));
		};
		
		public void writeReturnStatementBytecode(ClassWriter classWriter/*, MemberFunction declaration*/, MethodVisitor mv, ReturnStatement statement, JvmScope scope) {
			
			// -- write return expression
			new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, statement.getExpression(), scope);

			// -- write return
			org.sirius.frontend.api.Type type = statement.getExpressionType();
			
			if(type instanceof IntegerType) {
			    mv.visitInsn(ARETURN);	// TODO ???
//			    mv.visitInsn(IRETURN);
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
			
//			write
			
//			reporter.info(">>> IfElseStatement (" + expr + ")" + ifStmt );
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
				LocalVariableStatement st = h.getStatement();
				int locvarIndex = h.getIndex();
				Optional<Expression> optInitExp = st.getInitialValue();
				if(optInitExp.isPresent()) {
					Expression expr = optInitExp.get();
					new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, expr, scope);
					
					mv.visitVarInsn(Opcodes.ASTORE, locvarIndex);

				}
			}
			
			public void writeByteCode(ClassWriter classWriter, MethodVisitor mv) {
				
				JvmScope scope = new JvmScope(descriptorFactory);
				
				
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
					}
					else if(st instanceof IfElseStatement) {
						writeIfElseStatementBytecode(classWriter, mv, (IfElseStatement)st, scope);
					}
				}

				scope.markEnd();
				
				scope.writeLocalVariableStatements(classWriter, mv);
			}
		}
		
		private void writeFunctionArgs(ClassWriter classWriter, MethodVisitor mv) {
			for(FunctionFormalArgument arg: memberFunction.getArguments()) {
				Type type = arg.getType();
			}
		}
		
		public void writeBytecode(ClassWriter classWriter/*, MemberFunction declaration*/) {

			String functionName = memberFunction.getQName().getLast();
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

			writeFunctionArgs(classWriter, mv);

			JvmStatementBlock bodyBlock = new JvmStatementBlock(/*memberFunction.getBodyStatements()*/optBody.get());
			bodyBlock.writeByteCode(classWriter, mv);

			writeDummyDefaultReturn(mv);
			
//			mv.visitInsn(RETURN);
			mv.visitMaxs(-1, -1);
			mv.visitEnd();
		}
	}