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

import java.util.Optional;

import org.objectweb.asm.ClassWriter;
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
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.TypeCastExpression;

public class JvmMemberFunction {
		private AbstractFunction memberFunction;
		private boolean isStatic;
		private DescriptorFactory descriptorFactory;
		private Reporter reporter;

		
		public JvmMemberFunction(Reporter reporter, DescriptorFactory descriptorFactory, AbstractFunction memberFunction, boolean isStatic) {
			super();
			this.reporter = reporter;
			this.descriptorFactory = descriptorFactory;
			this.memberFunction = memberFunction;
			this.isStatic = isStatic;
		};

		public void writeExpressionBytecode(MethodVisitor mv, Expression expression) {
			if(expression instanceof FunctionCall) {
				FunctionCall call = (FunctionCall)expression;
				processFunctionCall(mv, call);
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
				processBinaryOpExpression(mv, (BinaryOpExpression)expression);
			} else if(expression instanceof ConstructorCall) {
				processConstructorCall(mv, (ConstructorCall) expression);
			}
		}
		private void processStringConstant(MethodVisitor mv, StringConstantExpression expression) {
		    mv.visitLdcInsn(expression.getText());
		}
		
		private void processIntegerConstant(MethodVisitor mv, IntegerConstantExpression expression) {
		    mv.visitLdcInsn(expression.getValue());
		}
		public void processBinaryOpExpression(MethodVisitor mv, BinaryOpExpression expression) {
//			MethodVisitor mv = methodStack.peek().mv;
			writeExpressionBytecode(mv, expression.getLeft());
			writeExpressionBytecode(mv, expression.getRight());
			
			BinaryOpExpression.Operator operator = expression.getOperator();
			
			switch(operator) {
			case Add:
				mv.visitInsn(IADD);
				break;
			case Mult:
				mv.visitInsn(IMUL);
				break;
			case Substract:
				mv.visitInsn(ISUB);
				break;
			case Divide:
				mv.visitInsn(IDIV);
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
		
		private void processFunctionCall(MethodVisitor mv, FunctionCall call) {
			String funcName = call.getFunctionName().getText();
			
			if(funcName.equals("println")) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

				for(Expression argExpr:call.getArguments()) {
					writeExpressionBytecode(mv, argExpr);
				}

				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false /*isInterface*/);
			} else {
				
				call.getArguments().forEach(expr -> writeExpressionBytecode(mv, expr) );

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

		
		
		
		
		public void writeReturnStatementBytecode(ClassWriter classWriter/*, MemberFunction declaration*/, MethodVisitor mv, ReturnStatement statement) {
			
			// -- write return expression
			writeExpressionBytecode(mv, statement.getExpression());

			// -- write return
			org.sirius.frontend.api.Type type = statement.getExpressionType();
			if(type instanceof IntegerType) {
			    mv.visitInsn(IRETURN);
			} else if(type instanceof ClassType) {
			    mv.visitInsn(ARETURN);
			} else {
				reporter.error("Currently unsupported expression type in return statement: " + type);
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

			for(Statement st: memberFunction.getBodyStatements() ) {
				if(st instanceof ReturnStatement)
					writeReturnStatementBytecode(classWriter, mv, (ReturnStatement)st);
			}
			
//			mv.visitInsn(RETURN);
			mv.visitMaxs(-1, -1);
			mv.visitEnd();
		}
	}