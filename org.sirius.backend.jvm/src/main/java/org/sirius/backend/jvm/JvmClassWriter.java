package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.api.Visitor;

/** Convert a class declaration in equivalent bytecode.
 * 
 * @author jpragey
 *
 */
public class JvmClassWriter {

//	private Optional<String> classDir;
	private List<ClassWriterListener> listeners;
	private Reporter reporter;
	private boolean verboseAst;
	private DescriptorFactory descriptorFactory;
	
	public JvmClassWriter(Reporter reporter, /*Optional<String> classDir, */List<ClassWriterListener> listeners, boolean verboseAst) {
		super();
		this.reporter = reporter;
//		this.classDir = classDir;
		this.listeners = listeners;
		this.verboseAst = verboseAst;
		this.descriptorFactory = new DescriptorFactory(reporter);
	}

	
	public Bytecode createByteCode(ClassDeclaration classDeclaration) {
		CodeCreationVisitor visitor = new CodeCreationVisitor();
		classDeclaration.visitMe(visitor);
		
		byte[] bytes = visitor.toByteCode();
		Bytecode bytecode = new Bytecode(bytes, classDeclaration.getQName());
		
		listeners.forEach(l -> l.addByteCode(bytecode, classDeclaration.getQName()));
		
		return bytecode;
	}

	public static String classInternalName(ClassDeclaration classDeclaration) {
		/* From doc:
		 * Returns the internal name of the class corresponding to this object or array type. 
		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
		 * This method should only be used for an object or array type.
		 */
		String in = classDeclaration.getQName().getStringElements().stream().collect(Collectors.joining("/"));
		return in;
	}
	
	

	class CodeCreationVisitor implements Visitor {
		private final static int VERSION = 49;

		ClassWriter classWriter;
		
		List<String> definedClasses = new ArrayList<>();

		public void startClass(ClassDeclaration declaration) {

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

			String classInternalName = classInternalName(declaration);

			classWriter.visit(49, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
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

			this.classWriter.visitSource("Hello.java", null /*debug*/);

			startClass(classDeclaration);

			
//			System.out.println(" MemberFunction count: " + classDeclaration.getFunctions().size());
//			for(MemberFunction function: classDeclaration.getFunctions()) {
//				System.out.println(" MemberFunction: " + function.getQName() + " , statements: " + function.getBodyStatements());
//			}

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
			
		}
		public byte[] toByteCode() {
			return classWriter.toByteArray();
		}

		Stack<MethodVisitor> methodStack = new Stack<>();
		
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
			
			methodStack.push(mv);
		}

		@Override
		public void end(MemberFunction functionDeclaration) {
			
			MethodVisitor mv = methodStack.pop();
		    mv.visitInsn(RETURN);
		    mv.visitMaxs(2, 1);
		    mv.visitEnd();
		}
		
		@Override
		public void start(Statement statement) {
			System.out.println("Starting statement " + statement);
			MethodVisitor mv = methodStack.peek();
			
//			if(statement instanceof IntegerConstantExpression) {
////				Integer stmtValue = ((IntegerConstantExpression) statement).getValue();
////				mv.visitLdcInsn(stmtValue);
//			} else {
				reporter.error("Backend: statement type not supported: " + statement);
//			}
			
		}
//		@Override
		public void end(Statement statement) {
			System.out.println("Ending statement " + statement);
		}

		@Override
		public void start(ReturnStatement statement) {
			MethodVisitor mv = methodStack.peek();
//		    mv.visitInsn(ICONST_5);
//		    mv.visitInsn(IRETURN);
//	    mv.visitMaxs(2, 1);
//	    mv.visitEnd();
			System.out.println("start ReturnStatement " + statement);
		}
		public void end(ReturnStatement statement) {
			MethodVisitor mv = methodStack.peek();
		    mv.visitInsn(IRETURN);
//	    mv.visitMaxs(2, 1);
//	    mv.visitEnd();
			System.out.println("end ReturnStatement " + statement);
		}
		
		@Override
		public void start(ExpressionStatement statement) {
			Expression expression = statement.getExpression(); 
//			System.out.println("Starting ExpressionStatement " + statement + ", expression: " + expression);
			MethodVisitor mv = methodStack.peek();

			processExpression(mv, expression);
//			if(expression instanceof FunctionCall) {
//				FunctionCall call = (FunctionCall)expression;
//				processFunctionCall(mv, call);
//			} else {
//				reporter.error("Currently unsupported expression type: " + expression.getClass());
//			}
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
			MethodVisitor mv = methodStack.peek();
		    mv.visitLdcInsn(expression.getValue());
		}
		@Override
		public void end(IntegerConstantExpression expression) {
			
		}

		private void processFunctionCall(MethodVisitor mv, FunctionCall call) {
			String funcName = call.getFunctionName().getText();
			if(funcName.equals("println")) {


				// -- System.out.println("...")
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
		
	}
}
