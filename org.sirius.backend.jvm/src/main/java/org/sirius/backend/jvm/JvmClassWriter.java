package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Visitor;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstVisitor;

/** Convert a class declaration in equivalent bytecode.
 * 
 * @author jpragey
 *
 */
public class JvmClassWriter {

	private Optional<String> classDir;
	private Reporter reporter;
	
	public JvmClassWriter(Reporter reporter, Optional<String> classDir) {
		super();
		this.reporter = reporter;
		this.classDir = classDir;
	}

	
	public Bytecode createByteCode(ClassDeclaration classDeclaration) {
		CodeCreationVisitor visitor = new CodeCreationVisitor();
		classDeclaration.visitMe(visitor);
		
		byte[] byteCode = visitor.toByteCode();
		return new Bytecode(byteCode);
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
//
//		public byte[] getByteCode() {
//			return byteCode;
//		}
//
//		@Override
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
		
//		private void startClass(/*ClassWriter cw, */AstClassDeclaration classDeclaration) {
//
//			/* Flags for class/interface:
//			 * @See https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1
//			 * 
//			 *  Flag Name 		Interpretation
//			 *  
//			ACC_PUBLIC 		Declared public; may be accessed from outside its package.
//			ACC_FINAL 		Declared final; no subclasses allowed.
//			ACC_SUPER 		Treat superclass methods specially when invoked by the invokespecial instruction.
//			ACC_INTERFACE 	Is an interface, not a class.
//			ACC_ABSTRACT 	Declared abstract; must not be instantiated.
//			ACC_SYNTHETIC 	Declared synthetic; not present in the source code.
//			ACC_ANNOTATION 	Declared as an annotation type.
//			ACC_ENUM 		Declared as an enum type.
//			ACC_MODULE		Is a module, not a class or interface.
//			 */
//			int access = ACC_SUPER; // Always use ACC_SUPER ! 
//			////		if(classDeclaration.getVisibility() == Visibility.PUBLIC)
//			access |= ACC_PUBLIC;
//
//			String classInternalName = classInternalName(classDeclaration);
//
//			classWriter.visit(49, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
//		}

//		public List<String> getDefinedClasses() {
//			return definedClasses;
//		}

		@Override
		public void start(ClassDeclaration classDeclaration) {
			System.out.println(" -- Starting ClassDeclaration " + classDeclaration.getQName());

			String clssQname = classDeclaration.getQName().dotSeparated();
			this.definedClasses.add(clssQname);
			
			this.classWriter = new ClassWriter(
					ClassWriter.COMPUTE_FRAMES | // No need to 
					ClassWriter.COMPUTE_MAXS // You must still call visitMaxs(), but its args are ignored 
					/*0*/ /*flags*/
					);

			this.classWriter.visitSource("Hello.java", null /*debug*/);

			startClass(classDeclaration);

			MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
//			MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()I", null, null);
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
			System.out.println(" -- Ending ClassDeclaration " + declaration.getQName());
			
			classWriter.visitEnd();
			
		}
		public byte[] toByteCode() {
			return classWriter.toByteArray();
		}

		@Override
		public void start(MemberFunction declaration) {
//			// TODO Auto-generated method stub
//			Visitor.super.start(declaration);
//		}
//		public void start(FunctionDeclaration  functionDeclaration) {
			System.out.println(" -- Starting TopLevelFunction " + declaration.getQName());
		}

		@Override
		public void end(MemberFunction functionDeclaration) {
			System.out.println(" -- Exit FunctionDeclaration " + functionDeclaration.getQName());
			MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC,
		            "main",
		//            "([Ljava/lang/String;)V",
//		            "(Ljava/lang/String;)V",
		            "(Ljava/lang/String;)I",
		            null /* String signature */,
		            null /* String[] exceptions */);
		    mv.visitFieldInsn(GETSTATIC,
		            "java/lang/System",
		            "out",
		            "Ljava/io/PrintStream;");
		    mv.visitLdcInsn("hello");
		    mv.visitMethodInsn(INVOKEVIRTUAL,
		            "java/io/PrintStream",
		            "println",
		            "(Ljava/lang/String;)V",
		            false /*isInterface*/);
//		    mv.visitInsn(RETURN);
		    
		    mv.visitLdcInsn(42);
		    mv.visitInsn(IRETURN);
		    
		    mv.visitMaxs(2, 1);
		    mv.visitEnd();
		}

////		@Override
////		public void startConstantExpression(ConstantExpression expression) {
////			System.out.println(" -- Starting ConstantExpression " + expression.getContent().getText());
////		}
////
////		@Override
////		public void endConstantExpression(ConstantExpression expression) {
////			System.out.println(" -- Ending ConstantExpression " + expression.getContent().getText());
////		}
//
//		@Override
//		public void startReturnStatement(AstReturnStatement statement) {
//			System.out.println(" -- Starting ReturnStatement " + statement.getExpression());
//		}
//
//		@Override
//		public void endReturnStatement(AstReturnStatement statement) {
//			System.out.println(" -- Ending ReturnStatement " + statement.getExpression());
//		}

		
	}
}
