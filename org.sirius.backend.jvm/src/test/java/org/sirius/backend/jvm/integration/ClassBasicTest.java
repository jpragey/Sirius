package org.sirius.backend.jvm.integration;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

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

//	public Object compileRunAndReturn(String script) throws Exception {
//		
//		ScriptSession session = CompileTools.compileScript(script, reporter);
//		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
//		InMemoryClassWriterListener l = backend.addInMemoryOutput();
//		
//		backend.process(session);
//		
//		HashMap<String, Bytecode> map = l.getByteCodesMap();
//		System.out.println(map.keySet());
//		session.getGlobalSymbolTable().dump();
//
//		
//		ClassLoader classLoader = l.getClassLoader();
//		
//		String mainClassQName = "$package$"; 
//		
//		Class<?> cls = classLoader.loadClass(mainClassQName);
//		Object helloObj = cls.getDeclaredConstructor().newInstance();
//		Method[] methods = helloObj.getClass().getDeclaredMethods();
//
//		for(Method m: methods)
//			System.out.println("Method: " + m);
//
//		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
////		System.out.println("Main: " + main);
//		
//		Object[] argTypes = new Object[] {};
//		
//		Object result = main.invoke(null, argTypes /*, args*/);
//		System.out.println("Result: " + result);
//		
//		return result;
//	}

	@Test(description = "Create a class instance (in a return expression)")
	public void classTest() throws Exception {
		String script = "#!\n "
				+ "class A() {Integer mi;}   "
				+ "A main() {return A();}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
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
		
		assertEquals(result.getClass().getName(), "A");

	}
	
	
	@Test(description = "access to a member value", enabled = false)
	public void fieldAccessTest() throws Exception {
		String script = "#!\n "
				+ "class B() {}   "
				+ "class A() {B mi = B();}   "
//				+ "Integer main() {return 42;}";
		+ "A main() {return A();}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
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
		
//		assertEquals(result, 42);
//		assertEquals(result, 0);

	}
	
	@Test(description = "", enabled = false)
	public void localVariableTest() throws Exception {
		String script = "#!\n "
		+ "class A(){}\n"
		+ "Integer main() {Integer a = 10; return a;}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
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
		
		assertEquals(result.getClass().getName(), "java.lang.Integer");
		assertEquals(result, 11);

	}
}
