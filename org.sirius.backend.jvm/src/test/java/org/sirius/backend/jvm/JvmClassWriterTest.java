package org.sirius.backend.jvm;

import static org.testng.Assert.assertEquals;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.util.TraceClassVisitor;
import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.VoidType;
import org.sirius.frontend.api.testimpl.ClassDeclarationTestImpl;
import org.sirius.frontend.api.testimpl.ClassTypeImpl;
import org.sirius.frontend.api.testimpl.ConstructorCallImpl;
import org.sirius.frontend.api.testimpl.IntegerConstantExpressionTestImpl;
import org.sirius.frontend.api.testimpl.IntegerTypeTestImpl;
import org.sirius.frontend.api.testimpl.LocalVariableStatementTestImpl;
import org.sirius.frontend.api.testimpl.MemberFunctionTestImpl;
import org.sirius.frontend.api.testimpl.MemberValueImpl;
import org.sirius.frontend.api.testimpl.ReturnStatementTestImpl;
import org.sirius.frontend.api.testimpl.StringConstantExpressionTestImpl;
import org.sirius.frontend.api.testimpl.StringTypeTestImpl;
import org.sirius.frontend.ast.AstToken;
import org.testng.annotations.Test;

public class JvmClassWriterTest {
	
	@Test
	public void classInternalNameTest() {
		ClassDeclaration cd = new ClassDeclarationTestImpl(
				new QName("a", "b", "C"),
				Collections.emptyList(), 
				Collections.emptyList());
		
		assertEquals( JvmClassWriter.classInternalName(cd), "a/b/C");
	}
	
	@Test 
	public void simpleBytecodeCreationTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter()); 
		
		ClassDeclaration cd = new ClassDeclarationTestImpl(
				new QName("a", "b", "C"),
				Collections.emptyList(),
				Arrays.asList(new MemberFunctionTestImpl(new QName("a", "b", "C", "main")))
		);
		JvmClassWriter writer = new JvmClassWriter(reporter /*, Optional.empty() *//*class dir*/, Collections.emptyList(), false /* verbose 'ast' */);
		Bytecode bytecode = writer.createByteCode(cd);
		System.out.println("bytecode: " + bytecode.size() + " bytes");
//		assertEquals( JvmClassWriter.classInternalName(cd), "a/b/C");

		// -- Check bytecode by loading it
		MyClassLoader classloader = new MyClassLoader(getClass().getClassLoader(), bytecode.getBytes(), Arrays.asList("a.b.C")/*definedClasses*/);

		//@SuppressWarnings("rawtypes")
		Class<Object> cls = (Class<Object>)classloader.loadClass("a.b.C" /*mainClassQName*/);
//		Object helloObj = cls.newInstance();
		Object helloObj = cls.getDeclaredConstructor(/*parameterTypes*/).newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
		assertEquals(methods.length, 1);
//		assertEquals(methods[0].toString(), "public static int a.b.C.main(java.lang.String)");
		assertEquals(methods[0].toString(), "public static void a.b.C.main()");
		assertEquals(methods[0].getName(), "main");

		for(Method m: methods)
			System.out.println("Method: " + m);

		Method mainMethod = methods[0];
		mainMethod.invoke(null/*ignored(static func*/ /*, args */);
		
////		Object result = runBytecode(bytecode, Arrays.asList("a.b.C")/*definedClasses*/, "a.b.C" /*mainClassQName*/);
	}

	@SuppressWarnings("unchecked") // Because of classloader.loadClass()
	@Test
	public void functionReturningInt() throws Exception {
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter()); 
		
		Statement retStmt = new ReturnStatementTestImpl(new IntegerConstantExpressionTestImpl(42));
		MemberFunction memberFunction = new MemberFunctionTestImpl(
						new QName("a", "b", "C", "main"), 
						Collections.emptyList(), //List<FunctionFormalArgument> formalArguments, 
						new IntegerTypeTestImpl(),
						Arrays.asList(retStmt)
						);
		
		ClassDeclaration cd = new ClassDeclarationTestImpl(
				new QName("a", "b", "C"),
				Collections.emptyList(),
				Arrays.asList(memberFunction));
		
		JvmClassWriter writer = new JvmClassWriter(reporter /*, Optional.empty() *//*class dir*/, Collections.emptyList(), false /* verbose 'ast' */);
		Bytecode bytecode = writer.createByteCode(cd);
		System.out.println("bytecode: " + bytecode.size() + " bytes");
//		assertEquals( JvmClassWriter.classInternalName(cd), "a/b/C");

		// -- Check bytecode by loading it
		MyClassLoader classloader = new MyClassLoader(getClass().getClassLoader(), bytecode.getBytes(), Arrays.asList("a.b.C")/*definedClasses*/);

		Class<Object> cls = (Class<Object>)classloader.loadClass("a.b.C" /*mainClassQName*/);
		Object helloObj = cls.getDeclaredConstructor(/*parameterTypes*/).newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
		assertEquals(methods.length, 1);
		assertEquals(methods[0].toString(), "public static int a.b.C.main()");	// TODO: int ???
		assertEquals(methods[0].getName(), "main");

		for(Method m: methods)
			System.out.println("-- Method: " + m);

		Method mainMethod = methods[0];
		Object r = mainMethod.invoke(null/*ignored(static func*/ /*, args */);
		System.out.println("Result: " + r);
		
	}
	
	@Test
	public void functionReturningString() throws Exception {
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter()); 
		
		Statement retStmt = new ReturnStatementTestImpl(new StringConstantExpressionTestImpl(AstToken.internal("Hello World")));
		MemberFunction memberFunction = new MemberFunctionTestImpl(
						new QName("a", "b", "C", "main"), 
						Collections.emptyList(), //List<FunctionFormalArgument> formalArguments, 
						new StringTypeTestImpl(),
						Arrays.asList(retStmt)
						);
		
		ClassDeclaration cd = new ClassDeclarationTestImpl(
				new QName("a", "b", "C"),
				Collections.emptyList(),
				Arrays.asList(memberFunction));
		
		JvmClassWriter writer = new JvmClassWriter(reporter /*, Optional.empty() *//*class dir*/, Collections.emptyList(), false /* verbose 'ast' */);
		Bytecode bytecode = writer.createByteCode(cd);
//		System.out.println("bytecode: " + bytecode.size() + " bytes");

		// -- Check bytecode by loading it
		MyClassLoader classloader = new MyClassLoader(getClass().getClassLoader(), bytecode.getBytes(), Arrays.asList("a.b.C")/*definedClasses*/);

		Class<? extends Object> cls = (Class<? extends Object>)classloader.loadClass("a.b.C" /*mainClassQName*/);
//		Object helloObj = cls.newInstance();
		Object helloObj = cls.getDeclaredConstructor(/*parameterTypes*/).newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
		assertEquals(methods.length, 1);
//		assertEquals(methods[0].toString(), "public static int a.b.C.main(java.lang.String)");
		assertEquals(methods[0].toString(), "public static java.lang.String a.b.C.main()");	// TODO: int ???
		assertEquals(methods[0].getName(), "main");

		for(Method m: methods)
			System.out.println("-- Method: " + m);

		Method mainMethod = methods[0];
		Object r = mainMethod.invoke(null/*ignored(static func*/ /*, args */);
		System.out.println("Result: " + r);
		
	}

	@Test
	public void functionReturningClassInstance() throws Exception {
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter()); 

//		QName aQname = new QName("a", "b", "Dummy");
//		ClassDeclaration cdA = new ClassDeclarationTestImpl(
//				aQname,
//				Collections.emptyList(),
//				Arrays.asList());
//
//		Statement retStmt = new ReturnStatementTestImpl(
//				//new StringConstantExpressionTestImpl(AstToken.internal("Hello World"))
//				new ConstructorCallImpl(cdA, Arrays.asList() /*List<Expression> arguments*/)
//				);
		/**
		 * class a.b.CC {
		 * }
		 * class a.b.DD {
		 * 	 a.b.CC cc() {
		 *     return new a.b.CC();
		 *   }
		 * }
		 */
		ClassDeclaration cdCC = new ClassDeclarationTestImpl(new QName("a", "b", "CC"), Collections.emptyList()/*fields*/, Arrays.asList( /*memberFunction*/));
		ClassDeclaration cdDD = new ClassDeclarationTestImpl(new QName("a", "b", "DD"), Collections.emptyList(), Arrays.asList(
				new MemberFunctionTestImpl(
						new QName("a", "b", "CC", "cc"), Collections.emptyList() /* formal rags*/, 
						new ClassTypeImpl(new QName("a", "b", "CC")),
						Arrays.asList(new ReturnStatementTestImpl(
								new ConstructorCallImpl(cdCC, Arrays.asList() /*List<Expression> arguments*/)
								))
						)
				));
		
//		MemberFunction memberFunction = new MemberFunctionTestImpl(
//						new QName("a", "b", "DD", "newCC"), 
//						Collections.emptyList(), //List<FunctionFormalArgument> formalArguments, 
//						new ClassTypeImpl(new QName("a", "b", "CC")),
//						Arrays.asList(retStmt)
//						);
		
		
//		JvmClassWriter writer = new JvmClassWriter(reporter, Collections.emptyList(), false /* verbose 'ast' */);
		Bytecode bytecodeCC = createBytecode(cdCC, reporter);
		Bytecode bytecodeDD = createBytecode(cdDD, reporter);
//		Bytecode bytecodeCC = writer.createByteCode(cdCC);
		
		bytecodeCC.createClassFiles(reporter, "/tmp", new QName("a", "b", "CC"));
		bytecodeDD.createClassFiles(reporter, "/tmp", new QName("a", "b", "DD"));
		
		
		MyClassLoader cll = new MyClassLoader(getClass().getClassLoader(), bytecodeCC.getBytes(), Arrays.asList("a.b.CC"));
		cll.appendClassBytecode(bytecodeDD.getBytes(), Arrays.asList("a.b.DD"));
		
		Class<? extends Object> clsCC = cll.loadClass("a.b.CC");
		Class<? extends Object> clsDD = cll.loadClass("a.b.DD");
		Object newCC = clsCC.getDeclaredConstructor(/*parameterTypes*/).newInstance();
		Object newDD = clsDD.getDeclaredConstructor(/*parameterTypes*/).newInstance();


		Method[] methods = newDD.getClass().getDeclaredMethods();
		
		assertEquals(methods.length, 1);
//		assertEquals(methods[0].toString(), "public static int a.b.C.main(java.lang.String)");
		assertEquals(methods[0].toString(), "public static a.b.CC a.b.DD.cc()");	// TODO: int ???
		assertEquals(methods[0].getName(), "cc");

		for(Method m: methods)
			System.out.println("-- Method: " + m);

		Method mainMethod = methods[0];
		Object r = mainMethod.invoke(null/*ignored(static func*/ /*, args */);
		assertEquals(r.getClass().getName(), "a.b.CC");
//		System.out.println("Result: " + r);
	}

	@Test
	public void memberValueInClass() throws Exception {
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter()); 

		/**
		 * class a.b.CC {
		 * }
		 * class a.b.DD {
		 * 	 a.b.CC cc;
		 * }
		 */
		ClassDeclaration cdCC = new ClassDeclarationTestImpl(new QName("a", "b", "CC"), Collections.emptyList()/*fields*/, Arrays.asList( /*memberFunction*/));
		ClassDeclaration cdDD = new ClassDeclarationTestImpl(new QName("a", "b", "DD"), 
				Arrays.asList(
						new MemberValueImpl(cdCC, AstToken.internal("cc"), Optional.empty() /*initial value*/)
						), 
				Arrays.asList(
				new MemberFunctionTestImpl(
						new QName("a", "b", "CC", "cc"), Collections.emptyList() /* formal args*/, 
						new ClassTypeImpl(new QName("a", "b", "CC")),
						Arrays.asList(new ReturnStatementTestImpl(
								new ConstructorCallImpl(cdCC, Arrays.asList() /*List<Expression> arguments*/)
								))
						)
				));
		
		
//		JvmClassWriter writer = new JvmClassWriter(reporter, Collections.emptyList(), false /* verbose 'ast' */);
		Bytecode bytecodeCC = createBytecode(cdCC, reporter);
		Bytecode bytecodeDD = createBytecode(cdDD, reporter);
//		Bytecode bytecodeCC = writer.createByteCode(cdCC);
		
		bytecodeCC.createClassFiles(reporter, "/tmp", new QName("a", "b", "CC"));
		bytecodeDD.createClassFiles(reporter, "/tmp", new QName("a", "b", "DD"));
		
		
		MyClassLoader cll = new MyClassLoader(getClass().getClassLoader(), bytecodeCC.getBytes(), Arrays.asList("a.b.CC"));
		cll.appendClassBytecode(bytecodeDD.getBytes(), Arrays.asList("a.b.DD"));
		
		Class<? extends Object> clsCC = cll.loadClass("a.b.CC");
		Class<? extends Object> clsDD = cll.loadClass("a.b.DD");
		Object newCC = clsCC.getDeclaredConstructor().newInstance();
		Object newDD = clsDD.getDeclaredConstructor().newInstance();


		Method[] methods = newDD.getClass().getDeclaredMethods();
		
		assertEquals(methods.length, 1);
//		assertEquals(methods[0].toString(), "public static int a.b.C.main(java.lang.String)");
		assertEquals(methods[0].toString(), "public static a.b.CC a.b.DD.cc()");	// TODO: int ???
		assertEquals(methods[0].getName(), "cc");

		for(Method m: methods)
			System.out.println("-- Method: " + m);

		Method mainMethod = methods[0];
		Object r = mainMethod.invoke(null/*ignored(static func*/ /*, args */);
		assertEquals(r.getClass().getName(), "a.b.CC");
//		System.out.println("Result: " + r);
	}
	
	
	
	@Test
	public void functionLocalVariable() throws Exception {
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter()); 

		/**
		 * class a.b.CC {
		 * }
		 * class a.b.DD {
		 * 	 a.b.CC cc;
		 * }
		 */
		ClassDeclaration cdCC = new ClassDeclarationTestImpl(new QName("a", "b", "CC"), Collections.emptyList()/*fields*/, Arrays.asList( /*memberFunction*/));
		ClassDeclaration cdDD = new ClassDeclarationTestImpl(new QName("a", "b", "DD"), 
				Arrays.asList(
						new MemberValueImpl(cdCC, AstToken.internal("cc"), Optional.empty() /*initial value*/)
						), 
				Arrays.asList(
				new MemberFunctionTestImpl(
						new QName("a", "b", "CC", "cc"), 
						Collections.emptyList() /* formal args*/,
						
						VoidType.instance,
						Arrays.asList(
								new LocalVariableStatementTestImpl(cdCC, AstToken.internal("myvar"), Optional.empty() /*initialValue*/),
								new ReturnStatementTestImpl(new ConstructorCallImpl(cdCC, Arrays.asList())))
						)
				));
		
		
//		JvmClassWriter writer = new JvmClassWriter(reporter, Collections.emptyList(), false /* verbose 'ast' */);
		Bytecode bytecodeCC = createBytecode(cdCC, reporter);
		Bytecode bytecodeDD = createBytecode(cdDD, reporter);
//		Bytecode bytecodeCC = writer.createByteCode(cdCC);
		
		bytecodeCC.createClassFiles(reporter, "/tmp", new QName("a", "b", "CC"));
		bytecodeDD.createClassFiles(reporter, "/tmp", new QName("a", "b", "DD"));
		
//		
//		MyClassLoader cll = new MyClassLoader(getClass().getClassLoader(), bytecodeCC.getBytes(), Arrays.asList("a.b.CC"));
//		cll.appendClassBytecode(bytecodeDD.getBytes(), Arrays.asList("a.b.DD"));
//		
//		Class<? extends Object> clsCC = cll.loadClass("a.b.CC");
//		Class<? extends Object> clsDD = cll.loadClass("a.b.DD");
//		Object newCC = clsCC.getDeclaredConstructor().newInstance();
//		Object newDD = clsDD.getDeclaredConstructor().newInstance();
//
//
//		Method[] methods = newDD.getClass().getDeclaredMethods();
//		
//		assertEquals(methods.length, 1);
////		assertEquals(methods[0].toString(), "public static int a.b.C.main(java.lang.String)");
//		assertEquals(methods[0].toString(), "public static a.b.CC a.b.DD.cc()");	// TODO: int ???
//		assertEquals(methods[0].getName(), "cc");
//
//		for(Method m: methods)
//			System.out.println("-- Method: " + m);
//
//		Method mainMethod = methods[0];
//		Object r = mainMethod.invoke(null/*ignored(static func*/ /*, args */);
//		assertEquals(r.getClass().getName(), "a.b.CC");
////		System.out.println("Result: " + r);
	}

	
private static Bytecode createBytecode(ClassDeclaration cd, Reporter reporter) {
		JvmClassWriter writer = new JvmClassWriter(reporter, Collections.emptyList(), false /* verbose 'ast' */);
		Bytecode bytecode = writer.createByteCode(cd);
		return bytecode;
	}
	
	
	public static class MyClassLoader extends ClassLoader{
		
		private HashMap<String, byte[]> classMap = new HashMap<String, byte[]>();
		
		public void appendClassBytecode(byte[] classData, Collection<String> definedClasses) {
			definedClasses.stream().forEach(className -> {classMap.put(className, classData);});
		}
		
	    public MyClassLoader(ClassLoader parent, byte[] classData, Collection<String> definedClasses) {
	        super(parent);
	        appendClassBytecode(classData, definedClasses);
	    }

	    public Class<?> loadClass(String name) throws ClassNotFoundException {
	    	
	    	byte[] byteCode = classMap.get(name);
	    	Class<?> cls = (byteCode != null) ? defineClass(name, byteCode, 0, byteCode.length) : super.loadClass(name);

	    	return cls;
	    }
	}

	
}
