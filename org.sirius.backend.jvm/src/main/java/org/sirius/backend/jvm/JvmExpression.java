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
	private Expression expression;
	
	
	public JvmExpression(Reporter reporter, DescriptorFactory descriptorFactory, Expression expression) {
		super();
		this.reporter = reporter;
		this.descriptorFactory = descriptorFactory;
		this.expression = expression;
	}

	public void writeExpressionBytecode(MethodVisitor mv, JvmScope scope) {
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
		int argIndex = expression.paramIndex();
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
		Type sourceType = expression.type();
		Type targetType = expression.targetType();
		Expression sourceExpr = expression.expression();
		
		// TODO Implement it
		if(sourceType instanceof ClassType && targetType instanceof ClassType) {
			QName srcQName = ((ClassType)sourceType).qName();
			QName targetQName = ((ClassType)targetType).qName();
			if(srcQName .equals(targetQName)) {
				new JvmExpression(reporter, descriptorFactory, sourceExpr).writeExpressionBytecode(mv, scope);
				return;
			}
		}
		
		if(sourceType.equals(targetType)) {
			new JvmExpression(reporter, descriptorFactory, sourceExpr).writeExpressionBytecode(mv, scope);
		} else {
			reporter.warning("??? TypeCast (TODO)" + sourceType + " into " + targetType );
		}
	}
	
	private void processIntegerConstant(MethodVisitor mv, IntegerConstantExpression expression) {
		int expessionVal = expression.value();
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
		boolean expessionVal = expression.value();
		int opcode = expessionVal ?
				Opcodes.ICONST_1:
				Opcodes.ICONST_0;
		mv.visitInsn(opcode);
	}
	private void processBinaryOpExpression(MethodVisitor mv, BinaryOpExpression expression, JvmScope scope) {
		JvmExpression leftExpr  = new JvmExpression(reporter, descriptorFactory, expression.left());
		JvmExpression rightExpr = new JvmExpression(reporter, descriptorFactory, expression.right());
		
		leftExpr .writeExpressionBytecode(mv, scope);
		rightExpr.writeExpressionBytecode(mv, scope);

//		writeExpressionBytecode(mv, expression.getLeft(), scope);
//		writeExpressionBytecode(mv, expression.getRight(), scope);
		
		BinaryOpExpression.Operator operator = expression.operator();
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
				INVOKEVIRTUAL,			// opcode 
				"sirius/lang/Integer", 	// owner "java/io/PrintStream", 
				opFuncName, 			//"println", 
				"(Lsirius/lang/Integer;)Lsirius/lang/Integer;", // descriptor,	// "(Ljava/lang/String;)V",	// method descriptor 
				false /*isInterface*/);

		
	}
	public void processConstructorCall(MethodVisitor mv, ConstructorCall expression) {

		Type type = expression.type();
		if(type instanceof IntegerType) {
			String internalName = "sirius/lang/Integer";

			mv.visitTypeInsn(NEW, internalName);

			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false);
			
		} else {

			assert(type instanceof ClassType);
			String internalName = Util.classInternalName((ClassType)type);

			mv.visitTypeInsn(NEW, internalName);

			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false);
		}
	}

	public void processValueAccessExpression(MethodVisitor mv, MemberValueAccessExpression expression, JvmScope scope) {
		Expression containerExpr = expression.getContainerExpression();
		JvmExpression jvmContainerExpr = new JvmExpression(reporter, descriptorFactory, containerExpr);
		jvmContainerExpr.writeExpressionBytecode(mv, scope);
		
		Type containerType = containerExpr.type();
		MemberValue memberValue = expression.getMemberValue();
		
		String owner = Util.classInternalName((ClassType)containerType); // internal name x/y/A

		
		String name = memberValue.nameToken().getText();
		
		String descriptor = descriptorFactory.fieldDescriptor(memberValue.type());
		
		mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
		
	}
	public void processLocalVariableReference(MethodVisitor mv, LocalVariableReference varRef, JvmScope scope) {
		
		String varName = varRef.getName().getText();
		
		Optional<JvmScope.JvmLocalVariable> jvmLocalVar = scope.getVarByName(varName);
		jvmLocalVar.ifPresentOrElse( (locVar) -> {
			Type type = varRef.type();
			int varIndex = locVar.getIndex();

			if(type instanceof IntegerType) {
				mv.visitVarInsn(Opcodes.ALOAD, varIndex);
			} else  if(type instanceof ClassType) {
				mv.visitVarInsn(Opcodes.ALOAD, varIndex);
			} else {
				mv.visitVarInsn(Opcodes.ILOAD, varIndex);
			}
			
		}, ()->{
			reporter.error("(JVM backend): Internal error: local variable not found: " + varName, varRef.getName());
		});
	}

	private void processFunctionCall(MethodVisitor mv, FunctionCall call, JvmScope scope) {
		String funcName = call.nameToken().getText();

		AbstractFunction func = call.getDeclaration().get();	// TODO: check for absence
			if(func.getClassOrInterfaceContainerQName().isPresent()) {
			}
			int invokeOpcode;
			if(call.thisExpression().isPresent()) {
				invokeOpcode = INVOKEVIRTUAL;
			} else {
				invokeOpcode = INVOKESTATIC;
			}
			
			for(Expression expr: call.arguments()) {
				JvmExpression jvmExpr = new JvmExpression(reporter, descriptorFactory, expr);
				jvmExpr.writeExpressionBytecode(mv, scope);
			}

			Optional<AbstractFunction> topLevelFunc = call.getDeclaration();
			if(topLevelFunc.isPresent()) {
				String methodDescriptor = descriptorFactory.methodDescriptor(topLevelFunc.get());
				Optional<QName> optContainerQName = func.getClassOrInterfaceContainerQName();
				
				String owner;	// eg "org/sirius/backend/jvm/bridge/TopLevel"
				if(optContainerQName.isPresent()) {
					QName containerQName = optContainerQName.get();
					owner = containerQName.getStringElements().stream().collect(Collectors.joining("/"));
					
				} else {

					owner = Util.jvmPackageClassName;					// returning int: this line alone is OK
					
					if(funcName.equals("println")) { // TODO: ARGHHH !!!

						if(/*owner.equals("sirius/lang") &&*/ invokeOpcode == INVOKESTATIC) {
							owner = "org/sirius/backend/jvm/bridge/TopLevel";
						}
					}
				}
//				String owner = optContainerQName.isPresent() ?
//						optContainerQName.get().getStringElements().stream().collect(Collectors.joining("/")) :
//							Util.jvmPackageClassName /*"$package$"*/;
				
//				if(owner.equals("sirius/lang") && invokeOpcode == INVOKESTATIC) {
//					owner = "org/sirius/backend/jvm/bridge/TopLevel";
//				}
//				org.sirius.backend.jvm.bridge
				
//				String owner = func.getClassOrInterfaceContainerQName().flatMap(qname -> "");
//						..get "$package$";		// owner "java/io/PrintStream",;
				mv.visitMethodInsn(
						invokeOpcode,		// opcode 
						owner,		// owner "java/io/PrintStream", 
						call.nameToken().getText(), //"println", 
						methodDescriptor,			// "(Ljava/lang/String;)V",	// method descriptor 
						false 				// isInterface
						);
			} else {
				reporter.error("Backend: top-level function not defined: " + funcName);
			}
		}
//	}
	
}
