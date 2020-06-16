package org.sirius.frontend.dummy;

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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.AstReturnStatement;


public class DummyGenerator {

	class CodeCreationVisitor implements AstVisitor {
		private final static int VERSION = 49;

		ClassWriter classWriter;
		byte[] byteCode;
		List<String> definedClasses = new ArrayList<>();

		public byte[] getByteCode() {
			return byteCode;
		}

		private String classInternalName(AstClassDeclaration classDeclaration) {
			/* From doc:
			 * Returns the internal name of the class corresponding to this object or array type. 
			 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
			 * This method should only be used for an object or array type.
			 */
			StringBuilder sb = new StringBuilder();
			for(String tk: classDeclaration.getQName().getStringElements()) {
				sb.append(tk);
				sb.append('/');
			}
			sb.append(classDeclaration.getName().getText());
			String internalName = sb.toString();

			return internalName;
		}

		private void startClass(/*ClassWriter cw, */AstClassDeclaration classDeclaration) {

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

			String classInternalName = classInternalName(classDeclaration);

			classWriter.visit(49, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
		}

		public List<String> getDefinedClasses() {
			return definedClasses;
		}

		public void startClassDeclaration (AstClassDeclaration classDeclaration) {
			System.out.println(" -- Starting ClassDeclaration " + classDeclaration.getName().getText());

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
		
		public void endClassDeclaration (AstClassDeclaration classDeclaration) {
			System.out.println(" -- Ending ClassDeclaration " + classDeclaration.getName().getText());
			
			classWriter.visitEnd();
			
			byte[] byteCode = classWriter.toByteArray();
			this.byteCode = byteCode;
		}

		@Override
		public void startFunctionDeclaration(AstFunctionDeclarationBuilder functionDeclaration) {
			System.out.println(" -- Starting FunctionDeclaration " + functionDeclaration.getName().getText());
		}

		@Override
		public void endFunctionDeclaration(AstFunctionDeclarationBuilder functionDeclaration) {
			System.out.println(" -- Exit FunctionDeclaration " + functionDeclaration.getName().getText());
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

//		@Override
//		public void startConstantExpression(ConstantExpression expression) {
//			System.out.println(" -- Starting ConstantExpression " + expression.getContent().getText());
//		}
//
//		@Override
//		public void endConstantExpression(ConstantExpression expression) {
//			System.out.println(" -- Ending ConstantExpression " + expression.getContent().getText());
//		}

		@Override
		public void startReturnStatement(AstReturnStatement statement) {
			System.out.println(" -- Starting ReturnStatement " + statement.getExpression());
		}

		@Override
		public void endReturnStatement(AstReturnStatement statement) {
			System.out.println(" -- Ending ReturnStatement " + statement.getExpression());
		}

		
	}
/***	
	@Test(enabled = true)
	public void simpleCodeGenerationTest() {

		Reporter reporter = new AccumulatingReporter(new ShellReporter());
		FrontEnd frontEnd = new FrontEnd(reporter);
		frontEnd.appendProviderInput(new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"));
		frontEnd.appendProviderInput(new TextInputTextProvider("a/b", "main.sirius", "run(){return \"hello\";}"));
		List<ModuleContent> moduleContents = frontEnd.parseAll();
		assertEquals(moduleContents.size(), 1);
		ModuleContent md = moduleContents.get(0);

		
		
//		ModuleContent md= Compiler.compile("run(){return \"hello\";}");
		CompilationUnit cu = md.getCompilationUnits().get(0);

		List<FunctionDeclaration> fds = cu.getFunctionDeclarations();
		assertEquals(fds.size(), 0);

		CodeCreationVisitor visitor = new CodeCreationVisitor();
		cu.visit(visitor);
		
		byte[] bytecode = visitor.getByteCode();
		assertNotNull(bytecode);
		int result = (int)runBytecode(bytecode, visitor.getDefinedClasses(), "$root$");
		assertNotNull(result);
		
		assertEquals(result, 42);
	}
*/
/***	
	public static class MyClassLoader extends ClassLoader{
		private byte[] classData;
		private HashSet<String> classQNames = new HashSet<String>();
		
	    public MyClassLoader(ClassLoader parent, byte[] classData, Collection<String> definedClasses) {
	        super(parent);
	        this.classData = classData;
	        classQNames.addAll(definedClasses);
	    }

	    public Class loadClass(String name) throws ClassNotFoundException {
//	    	String className = "pkg." + "Hello"; 
//	    	if(! (className) .equals(name))
//	    		return super.loadClass(name);

	    	if(! classQNames.contains(name))
	    		return super.loadClass(name);

	    	return defineClass(name,
	    			classData, 0, classData.length);
	    }
	}
*/
/***
	private Object runBytecode(byte[] bytecode, Collection<String> definedClasses, String mainClassQName) {
		try {
			MyClassLoader classloader = new MyClassLoader(getClass().getClassLoader(), bytecode, definedClasses);

			Class cls = classloader.loadClass(mainClassQName);
			Object helloObj = cls.newInstance();
			Method[] methods = helloObj.getClass().getDeclaredMethods();

			for(Method m: methods)
				System.out.println("Method: " + m);

			Method main = cls.getMethod("main", new Class[] {String.class});
			System.out.println("Main: " + main);
			Object result = main.invoke(null, "" /* args* /);
			System.out.println("Result: " + result);
			return result;
		} catch (Exception e) {
			System.out.println("Execution Exception: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	*/
	
	
	
//	private final static int VERSION = 49;
	
//	CompilationUnit compilationUnit;

//	public Backend(CompilationUnit compilationUnit) {
//		super();
//		this.compilationUnit = compilationUnit;
//	}
	
//	public byte[] createBytecode() {
//		ClassWriter cw = new ClassWriter(0 /*flags*/);
//		for(ClassDeclaration cd: compilationUnit.getClassDeclarations()) {
//			createBytecodeForClass(cw, cd);
//		}
//	     return cw.toByteArray();
//	}
//	
//	private String classInternalName(ClassDeclaration classDeclaration) {
//		/* From doc:
//		 * Returns the internal name of the class corresponding to this object or array type. 
//		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
//		 * This method should only be used for an object or array type.
//		 */
//		StringBuilder sb = new StringBuilder();
//		for(Token tk: classDeclaration.getPackageDeclaration().getQname()) {
//			sb.append(tk.getText());
//			sb.append('/');
//		}
//		sb.append(classDeclaration.getName().getText());
//		String internalName = sb.toString();
//		
////		String classInternalName = "pkg/" + classDeclaration.getClassName();
//		return internalName;
//	}
//	
//	private void startClass(ClassWriter cw, ClassDeclaration classDeclaration) {
//		
//		/* Flags for class/interface:
//		 * @See https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1
//		 * 
//		 *  Flag Name 		Interpretation
//		 *  
//			ACC_PUBLIC 		Declared public; may be accessed from outside its package.
//			ACC_FINAL 		Declared final; no subclasses allowed.
//			ACC_SUPER 		Treat superclass methods specially when invoked by the invokespecial instruction.
//			ACC_INTERFACE 	Is an interface, not a class.
//			ACC_ABSTRACT 	Declared abstract; must not be instantiated.
//			ACC_SYNTHETIC 	Declared synthetic; not present in the source code.
//			ACC_ANNOTATION 	Declared as an annotation type.
//			ACC_ENUM 		Declared as an enum type.
//			ACC_MODULE		Is a module, not a class or interface.
//		 */
//		int access = ACC_SUPER; // Always use ACC_SUPER ! 
//		if(classDeclaration.getVisibility() == Visibility.PUBLIC)
//			access |= ACC_PUBLIC;
//		
//		String classInternalName = classInternalName(classDeclaration);
//
//		cw.visit(49, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
//	}
//	
//	public byte[] createBytecodeForClass(ClassWriter cw, ClassDeclaration classDeclaration) {
//		
//
////		cw.visit(49, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
//		startClass(cw, classDeclaration);
//		
//		cw.visitSource("Hello.java", null /*debug*/);
//		
//		{
//	         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
//	         mv.visitVarInsn(ALOAD, 0);
//	         mv.visitMethodInsn(INVOKESPECIAL,
//	                 "java/lang/Object",
//	                 "<init>",
//	                 "()V", 
//	                 false /*isInterface*/);
//	         mv.visitInsn(RETURN);
//	         mv.visitMaxs(1, 1);
//	         mv.visitEnd();
//	     }
//	     {
//	    	 MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
//	                 "main",
////	                 "([Ljava/lang/String;)V",
//	                 "(Ljava/lang/String;)V",
//	                 null /* String signature */,
//	                 null /* String[] exceptions */);
//	         mv.visitFieldInsn(GETSTATIC,
//	                 "java/lang/System",
//	                 "out",
//	                 "Ljava/io/PrintStream;");
//	         mv.visitLdcInsn("hello");
//	         mv.visitMethodInsn(INVOKEVIRTUAL,
//	                 "java/io/PrintStream",
//	                 "println",
//	                 "(Ljava/lang/String;)V",
//	                 false /*isInterface*/);
//	         mv.visitInsn(RETURN);
//	         mv.visitMaxs(2, 1);
//	         mv.visitEnd();
//	     }
//	     cw.visitEnd();
//
//	     return cw.toByteArray();
//
//	}
//	

}
