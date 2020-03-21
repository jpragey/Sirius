package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.NOP;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ConstructorCall;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.api.Visitor;

public class CodeCreationVisitor implements Visitor {
//	private final static int VERSION = 49;
	private final static int VERSION = 52; // Java SE 8

	private ClassWriter classWriter;
	
	private List<String> definedClasses = new ArrayList<>();
	private Reporter reporter;
	private DescriptorFactory descriptorFactory;
	
	public static class MethodBuilder {
		public MethodVisitor mv;
		public int index = 0;

		public MethodBuilder(MethodVisitor mv) {
			super();
			this.mv = mv;
		}
		public int allocateIndex() {
			return index++;
		}
		
	}
	
	public static class ClassBuilder {
		public ClassDeclaration mv;

		public ClassBuilder(ClassDeclaration mv) {
			super();
			this.mv = mv;
		}
	}
	
	
//	private Stack<MethodVisitor> methodStack = new Stack<>();
	private Stack<MethodBuilder> methodStack = new Stack<>();
	private Stack<ClassBuilder> classStack = new Stack<>();

	
	public CodeCreationVisitor(Reporter reporter) {
		super();
		this.reporter = reporter;
		this.descriptorFactory = new DescriptorFactory(reporter);
		
//			TraceClassVisitor cv = new TraceClassVisitor(classWriter, new PrintWriter(System.out));
	}

	// ------ ClassDeclaration

	private void startClass(ClassDeclaration declaration) {

		/* Flags for class/interface:
		 * @See https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1
		 * 
		 *  Flag Name 		Interpretation
		 *  
		ACC_PUBLIC 		Declared public; may be accessed from outside its package.
		ACC_FINAL 		Declared final; no subclasses allowed.
		ACC_SUPER 		Treat superclass methods specially when invoked by the invokespecial instruction.
		ACC_INTERFACE 	Is an interface, not a class.
		ACC_ABSTRACT 	Declared abstract; must not be instantiated.
		ACC_SYNTHETIC 	Declared synthetic; not present in the source code.
		ACC_ANNOTATION 	Declared as an annotation type.
		ACC_ENUM 		Declared as an enum type.
		ACC_MODULE		Is a module, not a class or interface.
		 */
		int access = ACC_SUPER; // Always use ACC_SUPER ! 
		////		if(classDeclaration.getVisibility() == Visibility.PUBLIC)
		access |= ACC_PUBLIC;

		String classInternalName = JvmClassWriter.classInternalName(declaration);

		classStack.push(new ClassBuilder(declaration));
		
		classWriter.visit(VERSION, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
	}
	

	@Override
	public void start(ClassDeclaration classDeclaration) {
//			System.out.println(" -- Starting ClassDeclaration " + classDeclaration.getQName());

		String clssQname = classDeclaration.getQName().dotSeparated();
		this.definedClasses.add(clssQname);
		
		this.classWriter = new ClassWriter(
				ClassWriter.COMPUTE_FRAMES | // No need to 
				ClassWriter.COMPUTE_MAXS // You must still call visitMaxs(), but its args are ignored 
				/*0*/ /*flags*/
				);

		String fileName = classDeclaration.getQName().getLast() + ".java";
		this.classWriter.visitSource(fileName, null /*debug*/);
//			this.classWriter.visitSource("Hello.java", null /*debug*/);

		startClass(classDeclaration);

		writeInitMethod();
	}
	
	/** 
	 * 
	 */
	private void writeInitMethod() {
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL,
				"java/lang/Object",
				"<init>",
				"()V", 
				false /*isInterface*/);

		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	@Override
	public void end(ClassDeclaration declaration) {
//			System.out.println(" -- Ending ClassDeclaration " + declaration.getQName());
		
		classWriter.visitEnd();
		classStack.pop();
		
	}
	public byte[] toByteCode() {
		return classWriter.toByteArray();
	}

	
	@Override
	public void start(MemberFunction declaration) {
//			System.out.println(" -- Starting Member Function " + declaration.getQName());
		
		String functionName = declaration.getQName().getLast();
		String functionDescriptor = descriptorFactory.methodDescriptor(declaration);
		
//			System.out.println(" -- Exit FunctionDeclaration " + declaration.getQName());
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC,
				functionName, 
	            functionDescriptor,	// eg (Ljava/lang/String;)V
	            null /* String signature */,
	            null /* String[] exceptions */);
		
		MethodBuilder mb = new MethodBuilder(mv);
		methodStack.push(mb);
	}

	@Override
	public void end(MemberFunction functionDeclaration) {
		
		MethodVisitor mv = methodStack.pop().mv;
	    mv.visitInsn(RETURN);
	    mv.visitMaxs(-1, -1);
	    mv.visitEnd();
	}
	
	@Override
	public void start(Statement statement) {
		System.out.println("Starting statement " + statement);
		MethodVisitor mv = methodStack.peek().mv;
		
//			if(statement instanceof IntegerConstantExpression) {
////				Integer stmtValue = ((IntegerConstantExpression) statement).getValue();
////				mv.visitLdcInsn(stmtValue);
//			} else {
			reporter.error("Backend: statement type not supported: " + statement);
//			}
		
	}
	
	@Override
	public void end(Statement statement) {
		// System.out.println("Ending statement " + statement);
	}

	// ------ Return statement
	@Override
	public void start(ReturnStatement statement) {
		MethodVisitor mv = methodStack.peek().mv;
//		    mv.visitInsn(ICONST_5);
//		    mv.visitInsn(IRETURN);
//	    mv.visitMaxs(2, 1);
//	    mv.visitEnd();
		System.out.println("start ReturnStatement " + statement);
	}
	@Override
	public void end(ReturnStatement statement) {
		MethodVisitor mv = methodStack.peek().mv;
		
		org.sirius.frontend.api.Type type = statement.getExpressionType();
		if(type instanceof IntegerType) {
		    mv.visitInsn(IRETURN);
		} else if(type instanceof ClassType) {
		    mv.visitInsn(ARETURN);
		} else {
			reporter.error("Currently unsupported expression type in return statement: " + type);
		}
		
//	    mv.visitMaxs(2, 1);
//	    mv.visitEnd();
		System.out.println("end ReturnStatement " + statement);
	}
	
	@Override
	public void start(ExpressionStatement statement) {
		Expression expression = statement.getExpression(); 
		MethodVisitor mv = methodStack.peek().mv;

		processExpression(mv, expression);
	}

	private void processExpression(MethodVisitor mv, Expression expression) {
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
			TypeCastExpression tc = (TypeCastExpression)expression;
			processExpression(mv, tc.expression());
		} 
		
		
		else {
			reporter.error("Currently unsupported expression type: " + expression.getClass());
		}
	}
	private void processStringConstant(MethodVisitor mv, StringConstantExpression expression) {
	    mv.visitLdcInsn(expression.getText());
	}
	
	private void processIntegerConstant(MethodVisitor mv, IntegerConstantExpression expression) {
	    mv.visitLdcInsn(expression.getValue());
	}

	// -- Expressions
	@Override
	public void start(IntegerConstantExpression expression) {
		MethodVisitor mv = methodStack.peek().mv;
	    mv.visitLdcInsn(expression.getValue());
	}
	@Override
	public void end(IntegerConstantExpression expression) {
		
	}

	@Override
	public void start(StringConstantExpression expression) {
		MethodVisitor mv = methodStack.peek().mv;
		String text = expression.getText();
	    mv.visitLdcInsn(text);
	}
	@Override
	public void end(StringConstantExpression expression) {
		
	}
	
	public void start(ConstructorCall expression) {
		MethodVisitor mv = methodStack.peek().mv;
//			String internalName = descriptorFactory.methodDescriptor(expression.getName());
		String owner = expression.getName().slashSeparated();
		String name = expression.getName().getLast();
	    mv.visitTypeInsn(NEW, owner);
	    
	    mv.visitInsn(DUP);
	    

//            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "javax/naming/InitialContext", "<init>", "()V");
//            mv.visitVarInsn(Opcodes.ASTORE, 1);

//		    String owner = 
	    mv.visitMethodInsn(INVOKESPECIAL, 
	    		owner, //"a/b/A", //owner,		// the internal name of the method's owner class (see Type.getInternalName()).
	    		"<init>",	//name, 		//
	    		"()V", //descriptor,	//
	    		false /*isInterface*/);
	}
	public void end(ConstructorCall expression) {
		
	}
	
	private void processFunctionCall(MethodVisitor mv, FunctionCall call) {
		String funcName = call.getFunctionName().getText();
		
		if(funcName.equals("println")) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

			call.getArguments().forEach(expr -> processExpression(mv, expr) );

			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false /*isInterface*/);
		} else {
			
			call.getArguments().forEach(expr -> processExpression(mv, expr) );

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
			
//				reporter.error("Currently unsupported function (only 'println' is supported): " + funcName);
		}
	}
	
	@Override
	public void end(ExpressionStatement statement) {
//			System.out.println("Ending ExpressionStatement " + statement);
	}

	@Override
	public void start(MemberValue declaration) {
		String name = declaration.getName().getText();
		String descriptor = descriptorFactory.fieldDescriptor(declaration.getType());
		String signature = null;	// the field's signature. May be null if the field's type does not use generic types.
		String value = null;		// initial value (number or string), for static fields only
		
//		System.out.println("start(MemberValue)");
		classWriter.visitField(Opcodes.ACC_PUBLIC, name, descriptor, signature, value);
	}
	@Override
	public void end(MemberValue declaration) {
//		System.out.println("end(MemberValue)");
	}

	@Override
	public void start(LocalVariableStatement statement) {
		MethodBuilder mb = methodStack.peek();
		MethodVisitor mv = mb.mv;
		
		// -- Create variable
		int index = mb.allocateIndex();
		String name = statement.getName().getText();
		Type type = statement.getType();
		String descriptor = descriptorFactory.fieldDescriptor(type);
		String signature = null;
		Label start = new Label(); 
		Label end = new Label();
		mv.visitLabel(start);
		// -- Constructor
	    mv.visitInsn(NOP);

	    
		String owner = classStack.peek().mv.getQName().slashSeparated() + "/" + statement.getName();
	    mv.visitTypeInsn(NEW, owner);
	    
	    mv.visitInsn(DUP);
	    

//            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "javax/naming/InitialContext", "<init>", "()V");
//            mv.visitVarInsn(Opcodes.ASTORE, 1);

//		    String owner = 
	    mv.visitMethodInsn(INVOKESPECIAL, 
	    		owner, //"a/b/A", //owner,		// the internal name of the method's owner class (see Type.getInternalName()).
	    		"<init>",	//name, 		//
	    		"()V", //descriptor,	//
	    		false /*isInterface*/);
      mv.visitVarInsn(Opcodes.ASTORE, index);
	    
	    
	    // -- 
		mv.visitLabel(end);
		mv.visitLocalVariable(name, descriptor, signature, start, end, index);
		
		// -- 
		
//		System.out.println("start(LocalVariableStatement): name=" + name + " type: " + type);
	}
	@Override
	public void end(LocalVariableStatement statement) {
//		System.out.println("end(LocalVariableStatement)");
	}
	
}