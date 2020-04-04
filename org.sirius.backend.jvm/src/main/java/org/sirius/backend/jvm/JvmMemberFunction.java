package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.IDIV;
import static org.objectweb.asm.Opcodes.IMUL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.NEW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ClassOrInterfaceDeclaration;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ConstructorCall;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.LocalVariableReference;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.api.ValueAccessExpression;

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

		
		
		public void writeExpressionBytecode(MethodVisitor mv, Expression expression, JvmScope scope) {
			if(expression instanceof FunctionCall) {
				FunctionCall call = (FunctionCall)expression;
				processFunctionCall(mv, call, scope);
			} 
			else if(expression instanceof StringConstantExpression) {
				processStringConstant(mv, (StringConstantExpression) expression);
			} 
			else if(expression instanceof IntegerConstantExpression) {
				processIntegerConstant(mv, (IntegerConstantExpression) expression);
			} 
			else if(expression instanceof TypeCastExpression) {
//				TypeCastExpression tc = (TypeCastExpression)expression;
//				processExpression(mv, tc.expression());
			} else if(expression instanceof BinaryOpExpression) {
				processBinaryOpExpression(mv, (BinaryOpExpression)expression, scope);
			} else if(expression instanceof ConstructorCall) {
				processConstructorCall(mv, (ConstructorCall) expression);
			} else if(expression instanceof ValueAccessExpression) {
				processValueAccessExpression(mv, (ValueAccessExpression) expression, scope);
			} else if(expression instanceof LocalVariableReference) {
				processLocalVariableReference(mv, (LocalVariableReference) expression, scope);
			} else {
				throw new UnsupportedOperationException("Try to create bytecode for unknown expression : " + expression);
			}
		}
		private void processStringConstant(MethodVisitor mv, StringConstantExpression expression) {
		    mv.visitLdcInsn(expression.getText());
		}
		
		private void processIntegerConstant(MethodVisitor mv, IntegerConstantExpression expression) {
			int expessionVal = expression.getValue();
			boolean debugUseJavaInt = false;
			if(debugUseJavaInt) {
			    mv.visitLdcInsn(expessionVal);
			} else {	// use sirius.lang.Integer

				
				String internalName = "sirius/lang/Integer";

				mv.visitTypeInsn(NEW, internalName);

				mv.visitInsn(Opcodes.DUP);
				mv.visitIntInsn(Opcodes.BIPUSH, expessionVal);
				String initDescriptor = "(I)V";		// "()V" for void constructor
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", initDescriptor, false);

				
				
				
			}
		}
		public void processBinaryOpExpression(MethodVisitor mv, BinaryOpExpression expression, JvmScope scope) {
//			MethodVisitor mv = methodStack.peek().mv;
			writeExpressionBytecode(mv, expression.getLeft(), scope);
			writeExpressionBytecode(mv, expression.getRight(), scope);
			
			BinaryOpExpression.Operator operator = expression.getOperator();
			
			switch(operator) {
			case Add:
//				String descriptor = descriptorFactory.methodDescriptor(tlFunc.get());
				mv.visitMethodInsn(
						INVOKEVIRTUAL,	// opcode 
						"sirius/lang/Integer", // owner "java/io/PrintStream", 
						"add", //"println", 
						"(Lsirius/lang/Integer;)Lsirius/lang/Integer;", // descriptor,	// "(Ljava/lang/String;)V",	// method descriptor 
						false /*isInterface*/);
				
				//mv.visitInsn(IADD);
				break;
			case Mult:
				mv.visitMethodInsn(
						INVOKEVIRTUAL,	// opcode 
						"sirius/lang/Integer", // owner "java/io/PrintStream", 
						"mult", //"println", 
						"(Lsirius/lang/Integer;)Lsirius/lang/Integer;", // descriptor,	// "(Ljava/lang/String;)V",	// method descriptor 
						false /*isInterface*/);
//				mv.visitInsn(IMUL);
				break;
			case Substract:
				mv.visitMethodInsn(
						INVOKEVIRTUAL,	// opcode 
						"sirius/lang/Integer", // owner "java/io/PrintStream", 
						"sub", //"println", 
						"(Lsirius/lang/Integer;)Lsirius/lang/Integer;", // descriptor,	// "(Ljava/lang/String;)V",	// method descriptor 
						false /*isInterface*/);
//				mv.visitInsn(ISUB);
				break;
			case Divide:
				mv.visitMethodInsn(
						INVOKEVIRTUAL,	// opcode 
						"sirius/lang/Integer", // owner "java/io/PrintStream", 
						"div", //"println", 
						"(Lsirius/lang/Integer;)Lsirius/lang/Integer;", // descriptor,	// "(Ljava/lang/String;)V",	// method descriptor 
						false /*isInterface*/);
//				mv.visitInsn(IDIV);
				break;
			default:
				throw new UnsupportedOperationException("Binary operator not supported in JVM: " + operator);
			}
		}
		public void processConstructorCall(MethodVisitor mv, ConstructorCall expression) {

			Type type = expression.getType();
			assert(type instanceof ClassDeclaration);
			String internalName = Util.classInternalName((ClassDeclaration)type);

			mv.visitTypeInsn(NEW, internalName);

			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false);
		}

		public void processValueAccessExpression(MethodVisitor mv, ValueAccessExpression expression, JvmScope scope) {
			Expression containerExpr = expression.getContainerExpression();
			writeExpressionBytecode(mv, containerExpr, scope);
			
////			mv.visitInsn(Opcodes.DUP);
			String owner = "A"; // internal name
			String name = "mi";
			String descriptor = "B";
			mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
			
		}
		public void processLocalVariableReference(MethodVisitor mv, LocalVariableReference varRef, JvmScope scope) {
			
			String varName = varRef.getName().getText();
			Type type = varRef.getType();
			
			Optional<JvmScope.LocalVarHolder> h = scope.getVarByName(varName);
			if(h.isEmpty()) {
				reporter.error("(JVM backend): Internal error: loval variable not found: " + varName, varRef.getName());
				return;
			}
			
			int varIndex = h.get().getIndex();

			if(type instanceof ClassOrInterfaceDeclaration) {
				mv.visitVarInsn(Opcodes.ALOAD, varIndex);
			} else {
				mv.visitVarInsn(Opcodes.ILOAD, varIndex);
			}

			
//			Expression containerExpr = expression.getContainerExpression();
//			writeExpressionBytecode(mv, containerExpr);
//			
//			String owner = "A"; // internal name
//			String name = "mi";
//			String descriptor = "B";
//			mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
			
		}

		private void processFunctionCall(MethodVisitor mv, FunctionCall call, JvmScope scope) {
			String funcName = call.getFunctionName().getText();
			
			if(funcName.equals("println")) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

				for(Expression argExpr:call.getArguments()) {
					writeExpressionBytecode(mv, argExpr, scope);
				}

				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false /*isInterface*/);
			} else {
				
				call.getArguments().forEach(expr -> writeExpressionBytecode(mv, expr, scope));

				Optional<TopLevelFunction> tlFunc = call.getDeclaration();
				if(tlFunc.isPresent()) {
					String descriptor = descriptorFactory.methodDescriptor(tlFunc.get());
					mv.visitMethodInsn(
							INVOKEVIRTUAL,	// opcode 
							"$package$", // owner "java/io/PrintStream", 
							call.getFunctionName().getText(), //"println", 
							descriptor,	// "(Ljava/lang/String;)V",	// method descriptor 
							false /*isInterface*/);
				} else {
					reporter.error("Backend: top-level function not defined: " + funcName);
				}
			}
		}

		
		
		
		
		public void writeReturnStatementBytecode(ClassWriter classWriter/*, MemberFunction declaration*/, MethodVisitor mv, ReturnStatement statement, JvmScope scope) {
			
			// -- write return expression
			writeExpressionBytecode(mv, statement.getExpression(), scope);

			// -- write return
			org.sirius.frontend.api.Type type = statement.getExpressionType();
//			// TODO: remove
//			if(type instanceof ClassType && ((ClassType)type).getQName().dotSeparated().equals("sirius.lang.Integer") ) {
//			    mv.visitInsn(IRETURN);
//				return;
//			}
			
			if(type instanceof IntegerType) {
			    mv.visitInsn(ARETURN);	// TODO ???
//			    mv.visitInsn(IRETURN);
			} else if(type instanceof ClassType) {
			    mv.visitInsn(ARETURN);
			} else {
				reporter.error("Currently unsupported expression type in return statement: " + type);
			}
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
		
//		public void writeLocalVariableStatement(ClassWriter classWriter, MethodVisitor mv, LocalVariableStatement statement, 
//				JvmScope scope /*Label start, Label end*/) {
//			String name = statement.getName().getText();
//			
////			statement.getType();
//			String descriptor = descriptorFactory.fieldDescriptor(statement.getType());		// TODO: field ???      var descriptor
//			String signature = null; // the type signature of this local variable. May be {@literal null} if the local variable type does not use generic types.
////			Label start;
////			Label end;
//			int index=locvarIndex;
//			mv.visitLocalVariable(name, descriptor, signature, scope.getStartLabel(), scope.getEndLabel(), index);
//		}
			
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
					writeExpressionBytecode(mv, expr, scope);
					
					mv.visitVarInsn(Opcodes.ASTORE, locvarIndex);

				}
			}
			
			public void writeByteCode(ClassWriter classWriter, MethodVisitor mv) {
//				List<LocalVariableStatement> locVarsStmts = new ArrayList<>();
				//Label startLabel = new Label();
				
				JvmScope scope = new JvmScope(descriptorFactory);
				
				
				// Collect local variables to generate initialization code
				for(Statement st: statements ) {
					if(st instanceof LocalVariableStatement) { 
						LocalVariableStatement locVarsStmt = (LocalVariableStatement)st;
						scope.addLocalVariable(locVarsStmt);
						
//						writeLocalVarsInitCode(locVarsStmt, mv);
					}
				}
				// Write local var init code
				for(JvmScope.LocalVarHolder h: scope.getLocVarsStmts()) {
					writeLocalVarsInitCode(h, mv, scope);
				}

				
				for(Statement st: statements ) {
					if(st instanceof ReturnStatement) {
						writeReturnStatementBytecode(classWriter, mv, (ReturnStatement)st, scope);
//					} else if(st instanceof LocalVariableStatement) { 
////						writeLocalVariableStatement(classWriter, mv, (LocalVariableStatement)st);
//						locVarsStmts.add((LocalVariableStatement)st);
					}
				}

				scope.markEndScope();
				
//				Label endLabel = new Label();
//				for(LocalVariableStatement st : locVarsStmts) {
//					writeLocalVariableStatement(classWriter, mv, st, scope);
//				}
				scope.writeLocalVariableStatements(classWriter, mv);
				
			}
		}
		
		public void writeBytecode(ClassWriter classWriter/*, MemberFunction declaration*/) {

			String functionName = memberFunction.getQName().getLast();
			String functionDescriptor = descriptorFactory.methodDescriptor(memberFunction);	// eg (Ljava/lang/String;)V
			int access = ACC_PUBLIC;
			if(isStatic)
				access |= ACC_STATIC;
			
			MethodVisitor mv = classWriter.visitMethod(access, functionName, functionDescriptor,
					null /* String signature */,
					null /* String[] exceptions */);
			
			JvmStatementBlock bodyBlock = new JvmStatementBlock(memberFunction.getBodyStatements());
			bodyBlock.writeByteCode(classWriter, mv);
			
			// 
//			List<LocalVariableStatement> locVarsStmts = new ArrayList<>();
//			//Label startLabel = new Label();
//			
//			JvmScope scope = new JvmScope();
//			
//			for(Statement st: memberFunction.getBodyStatements() ) {
//				if(st instanceof ReturnStatement) {
//					writeReturnStatementBytecode(classWriter, mv, (ReturnStatement)st);
//				} else if(st instanceof LocalVariableStatement) { 
////					writeLocalVariableStatement(classWriter, mv, (LocalVariableStatement)st);
//					locVarsStmts.add((LocalVariableStatement)st);
//				}
//			}
//
//			scope.markEndScope();
//			
////			Label endLabel = new Label();
//			for(LocalVariableStatement st : locVarsStmts) {
//				writeLocalVariableStatement(classWriter, mv, st, scope);
//			}

			writeDummyDefaultReturn(mv);
			
			
//			mv.visitInsn(RETURN);
			mv.visitMaxs(-1, -1);
			mv.visitEnd();
		}
	}