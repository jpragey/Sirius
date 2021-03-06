package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;

import java.util.Optional;
import java.util.stream.Collectors;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.api.BooleanConstantExpression;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ConstructorCall;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionActualArgument;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.IntegerType;
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
			TypeCastExpression tc = (TypeCastExpression)expression;
			processTypeCast(mv, tc, scope);
		} else if(expression instanceof BinaryOpExpression) {
			processBinaryOpExpression(mv, (BinaryOpExpression)expression, scope);
		} else if(expression instanceof ConstructorCall) {
			processConstructorCall(mv, (ConstructorCall) expression);
		} else if(expression instanceof MemberValueAccessExpression) {
			processValueAccessExpression(mv, (MemberValueAccessExpression) expression, scope);
		} else if(expression instanceof LocalVariableReference) {
			processLocalVariableReference(mv, (LocalVariableReference) expression, scope);
		} else if(expression instanceof FunctionActualArgument) {
			processFunctionArgument(mv, (FunctionActualArgument) expression, scope);
		} else {
			throw new UnsupportedOperationException("Try to create bytecode for unknown expression : " + expression);
		}
	}

	private void processFunctionArgument(MethodVisitor mv, FunctionActualArgument expression, JvmScope scope/* TODO: ???*/) {
		int argIndex = expression.getIndex();
		mv.visitVarInsn(Opcodes.ALOAD, argIndex /*0 = this  locvarIndex*/);

		// -- put value
//		expression.
//		scope.
//		new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, expression, scope);

//		throw new UnsupportedOperationException("Try to create bytecode for FunctionActualArgument : " + expression);
	}

	private void processStringConstant(MethodVisitor mv, StringConstantExpression expression) {
//	    mv.visitLdcInsn(expression.getText());
		String expessionVal = expression.getText();
		String internalName = "sirius/lang/String";

		mv.visitTypeInsn(NEW, internalName);

		mv.visitInsn(Opcodes.DUP);
		mv.visitLdcInsn(expessionVal);
		String initDescriptor = "(Ljava/lang/String;)V";		// "()V" for void constructor
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", initDescriptor, false);
	}
	
	private void processTypeCast(MethodVisitor mv, TypeCastExpression expression, JvmScope scope) {
		Type sourceType = expression.getType();
		Type targetType = expression.targetType();
		Expression sourceExpr = expression.expression();
		
		// TODO Implement it
		if(sourceType instanceof ClassType && targetType instanceof ClassType) {
			QName srcQName = ((ClassType)sourceType).getQName();
			QName targetQName = ((ClassType)targetType).getQName();
			if(srcQName .equals(targetQName)) {
				new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, sourceExpr, scope);
				return;
			}
		}
		
		if(sourceType.equals(targetType)) {
			new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, sourceExpr, scope);
		} else {
			reporter.warning("??? TypeCast (TODO)" + sourceType + " into " + targetType );
		}
	}
	
	private void processIntegerConstant(MethodVisitor mv, IntegerConstantExpression expression) {
		int expessionVal = expression.getValue();
		boolean debugUseJavaInt = false;
		if(debugUseJavaInt) {
		    mv.visitLdcInsn(expessionVal);
		} else {	// use sirius.lang.Integer

//			mv.visitIntInsn(Opcodes.BIPUSH, expessionVal);
			
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
	private void processBinaryOpExpression(MethodVisitor mv, BinaryOpExpression expression, JvmScope scope) {
		writeExpressionBytecode(mv, expression.getLeft(), scope);
		writeExpressionBytecode(mv, expression.getRight(), scope);
		
		BinaryOpExpression.Operator operator = expression.getOperator();
		String opFuncName;
		switch(operator) {
		case Add:
			opFuncName = "add";
			break;
		case Mult:
			opFuncName = "mult";
			break;
		case Substract:
			opFuncName = "sub";
			break;
		case Divide:
			opFuncName = "div";
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
		if(type instanceof IntegerType) {
			String internalName = "sirius/lang/Integer";

			mv.visitTypeInsn(NEW, internalName);

			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false);
			
		} else {

			assert(type instanceof ClassType /*ClassDeclaration*/);
			String internalName = Util.classInternalName((ClassType)type);

			mv.visitTypeInsn(NEW, internalName);

			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false);
		}
	}

	public void processValueAccessExpression(MethodVisitor mv, MemberValueAccessExpression expression, JvmScope scope) {
		Expression containerExpr = expression.getContainerExpression();
		writeExpressionBytecode(mv, containerExpr, scope);
		
		Type containerType = containerExpr.getType();
		MemberValue memberValue = expression.getMemberValue();
		
		String owner = Util.classInternalName((ClassType)containerType); // internal name x/y/A

		
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

		if(type instanceof IntegerType) {
			mv.visitVarInsn(Opcodes.ALOAD, varIndex);
		} else  if(type instanceof ClassType) {
			mv.visitVarInsn(Opcodes.ALOAD, varIndex);
		} else {
			mv.visitVarInsn(Opcodes.ILOAD, varIndex);
		}

	}

	private void processFunctionCall(MethodVisitor mv, FunctionCall call, JvmScope scope) {
		String funcName = call.getFunctionName().getText();
		
//		if(funcName.equals("println")) {
//			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//
//			for(Expression argExpr:call.getArguments()) {
//				writeExpressionBytecode(mv, argExpr, scope);
//			}
//
//			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false /*isInterface*/);
//		} else {
			
			AbstractFunction func = call.getDeclaration().get();	// TODO: check for absence
			if(func.getClassOrInterfaceContainerQName().isPresent()) {
			}
			int invokeOpcode;
			if(call.getThis().isPresent()) {
				invokeOpcode = INVOKEVIRTUAL;
			} else {
				invokeOpcode = INVOKESTATIC;
			}
			
			for(Expression expr: call.getArguments()) {
//System.out.println("-------------------");
				writeExpressionBytecode(mv, expr, scope);
//System.out.println("-------------------");
			}

			Optional<AbstractFunction> topLevelFunc = call.getDeclaration();
			if(topLevelFunc.isPresent()) {
				String methodDescriptor = descriptorFactory.methodDescriptor(topLevelFunc.get());
//				String owner = "$package$";		// owner "java/io/PrintStream",;
				Optional<QName> optContainerQName = func.getClassOrInterfaceContainerQName();
				
				String owner = optContainerQName.isPresent() ?
						optContainerQName.get().getStringElements().stream().collect(Collectors.joining("/")) :
							Util.jvmPackageClassName /*"$package$"*/;
				
				if(owner.equals("sirius/lang") && invokeOpcode == INVOKESTATIC) {
					owner = "org/sirius/backend/jvm/bridge/TopLevel";
				}
//				org.sirius.backend.jvm.bridge
				
//				String owner = func.getClassOrInterfaceContainerQName().flatMap(qname -> "");
//						..get "$package$";		// owner "java/io/PrintStream",;
				mv.visitMethodInsn(
						invokeOpcode,		// opcode 
						owner,		// owner "java/io/PrintStream", 
						call.getFunctionName().getText(), //"println", 
						methodDescriptor,			// "(Ljava/lang/String;)V",	// method descriptor 
						false 				// isInterface
						);
			} else {
				reporter.error("Backend: top-level function not defined: " + funcName);
			}
		}
//	}
	
}
