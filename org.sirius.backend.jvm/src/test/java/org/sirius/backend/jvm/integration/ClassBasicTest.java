package org.sirius.backend.jvm.integration;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ClassBasicTest {

	private Reporter reporter;

	@BeforeMethod
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}


	@Test(description = "Create a class instance (in a return expression)")
	public void classTest() throws Exception {
		String script = "#!\n "
				+ "class A() {Integer mi;}   "
				+ "A main() {return A();}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
//		String mainClassQName = "A"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
//
//		for(Method m: methods)
//			System.out.println("Method: " + m);
		
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "A");

	}
	
	@Test(description = "Create a class instance from SDK (sirius.lang.Integer) (in a return expression)")
	public void classFromSDKTest() throws Exception {
		String script = "#!\n "
				+ "Integer main() {return Integer();}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
//		String mainClassQName = "A"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "sirius.lang.Integer");

	}
	
	
	@Test(description = "access to a member value", enabled = true)
	public void fieldAccessTest() throws Exception {
		String script = "#!\n "
//				+ "class B() {}   "
				+ "class A() {Integer mib = 42;}   "
//				+ "Integer main() {return 42;}";
				+ "Integer main() {A a = A(); return a.mib;}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.addFileOutput("/tmp/siriusTmp/module", Optional.of("/tmp/siriusTmp/classes"));

		backend.process(session);
		
//		HashMap<String, Bytecode> map = l.getByteCodesMap();
//		System.out.println(map.keySet());
//		session.getGlobalSymbolTable().dump();

		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
//		String mainClassQName = "A"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

//		System.out.println("Constructors:");
//		for(Constructor<?> c: cls.getConstructors())
//			System.out.println("  "+c);
//		
//		System.out.println("Fields:");
//		for(Field f: cls.getDeclaredFields())
//			System.out.println("  "+f);
		
		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
//
//		for(Method m: methods)
//			System.out.println("Method: " + m);
		
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
//
//		
//		Object result = compileRunAndReturn(script);
		
		assert(result instanceof sirius.lang.Integer);
		assertEquals(((sirius.lang.Integer)result ).value, 42);
//		assertEquals(result, 0);

	}
	
	@Test(description = "", enabled = true)
	public void localVariableTest() throws Exception {
		String script = "#!\n "
		+ "class A(){}\n"
//		+ "void main() {Integer a = 10;}";	// OK
//		+ "Integer main() {return 10;}";
		+ "Integer main() {Integer a = 10; return a;}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.addFileOutput("/tmp/siriusTmp/module", Optional.of("/tmp/siriusTmp/classes"));
		
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
//		String mainClassQName = "A"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "sirius.lang.Integer");
		assertEquals( ((sirius.lang.Integer)result).getValue(), 10);

	}
}
