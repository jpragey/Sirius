package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;

import java.util.Optional;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.api.BooleanConstantExpression;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ClassOrInterface;
import org.sirius.frontend.api.ConstructorCall;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.LocalVariableReference;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.MemberValueAccessExpression;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.TypeCastExpression;

public class JvmExpression {

	private Reporter reporter;
	private DescriptorFactory descriptorFactory;

	
	
	public JvmExpression(Reporter reporter, DescriptorFactory descriptorFactory) {
		super();
		this.reporter = reporter;
		this.descriptorFactory = descriptorFactory;
	}

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
		else if(expression instanceof BooleanConstantExpression) {
			processBooleanConstant(mv, (BooleanConstantExpression) expression);
		} 
		else if(expression instanceof TypeCastExpression) {
//			TypeCastExpression tc = (TypeCastExpression)expression;
//			processExpression(mv, tc.expression());
		} else if(expression instanceof BinaryOpExpression) {
			processBinaryOpExpression(mv, (BinaryOpExpression)expression, scope);
		} else if(expression instanceof ConstructorCall) {
			processConstructorCall(mv, (ConstructorCall) expression);
		} else if(expression instanceof MemberValueAccessExpression) {
			processValueAccessExpression(mv, (MemberValueAccessExpression) expression, scope);
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
	private void processBooleanConstant(MethodVisitor mv, BooleanConstantExpression expression) {
		boolean expessionVal = expression.getValue();
		int opcode = expessionVal ?
				Opcodes.ICONST_1:
				Opcodes.ICONST_0;
		mv.visitInsn(opcode);
	}
	public void processBinaryOpExpression(MethodVisitor mv, BinaryOpExpression expression, JvmScope scope) {
//		MethodVisitor mv = methodStack.peek().mv;
		writeExpressionBytecode(mv, expression.getLeft(), scope);
		writeExpressionBytecode(mv, expression.getRight(), scope);
		
		BinaryOpExpression.Operator operator = expression.getOperator();
		String opFuncName;
		switch(operator) {
		case Add:
			
//			String descriptor = descriptorFactory.methodDescriptor(tlFunc.get());
			opFuncName = "add";
			//mv.visitInsn(IADD);
			break;
		case Mult:
			opFuncName = "mult";
//			mv.visitInsn(IMUL);
			break;
		case Substract:
			opFuncName = "sub";
//			mv.visitInsn(ISUB);
			break;
		case Divide:
			opFuncName = "div";
//			mv.visitInsn(IDIV);
			break;
		default:
			throw new UnsupportedOperationException("Binary operator not supported in JVM: " + operator);
		}
		
		mv.visitMethodInsn(
				INVOKEVIRTUAL,	// opcode 
				"sirius/lang/Integer", // owner "java/io/PrintStream", 
				opFuncName, //"println", 
				"(Lsirius/lang/Integer;)Lsirius/lang/Integer;", // descriptor,	// "(Ljava/lang/String;)V",	// method descriptor 
				false /*isInterface*/);

		
	}
	public void processConstructorCall(MethodVisitor mv, ConstructorCall expression) {

		Type type = expression.getType();
		assert(type instanceof ClassDeclaration);
		String internalName = Util.classInternalName((ClassDeclaration)type);

		mv.visitTypeInsn(NEW, internalName);

		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false);
	}

	public void processValueAccessExpression(MethodVisitor mv, MemberValueAccessExpression expression, JvmScope scope) {
		Expression containerExpr = expression.getContainerExpression();
		writeExpressionBytecode(mv, containerExpr, scope);
		
		Type containerType = containerExpr.getType();
		MemberValue memberValue = expression.getMemberValue();
		
		String owner = Util.classInternalName((ClassDeclaration)containerType); // internal name x/y/A

		
		String name = memberValue.getName().getText();
		
		String descriptor = descriptorFactory.fieldDescriptor(memberValue.getType());
		
		mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
		
	}
	public void processLocalVariableReference(MethodVisitor mv, LocalVariableReference varRef, JvmScope scope) {
		
		String varName = varRef.getName().getText();
		Type type = varRef.getType();
		
		Optional<JvmScope.LocalVarHolder> h = scope.getVarByName(varName);
		if(h.isEmpty()) {
			reporter.error("(JVM backend): Internal error: local variable not found: " + varName, varRef.getName());
			return;
		}
		
		int varIndex = h.get().getIndex();

		if(type instanceof ClassOrInterface) {
			mv.visitVarInsn(Opcodes.ALOAD, varIndex);
		} else {
			mv.visitVarInsn(Opcodes.ILOAD, varIndex);
		}

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

			Optional<AbstractFunction> tlFunc = call.getDeclaration();
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
	
}
