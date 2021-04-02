package org.sirius.backend.jvm.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.backend.jvm.Util;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;

public class ClassBasicTest {

	private Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}


	@Test
	@DisplayName("Create a class instance (in a return expression)")
	public void classTest() throws Exception {
		String script = "#!\n "
				+ "class A() {Integer mi;}   "
				+ "A main() {return A();}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "A");

	}
	
	@Test
	@DisplayName("Create a class instance from SDK (sirius.lang.Integer) (in a return expression)")
	public void integerClassFromSDKTest() throws Exception {
		String script = "#!\n "
				+ "Integer main() {return Integer();}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName; 
//		String mainClassQName = "A"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "sirius.lang.Integer");

	}
	
	@Test
	@DisplayName("Create a s.l.String class instance from SDK (sirius.lang.Integer) (in a return expression)")
	public void stringClassFromSDKTest() throws Exception {
		String script = "#!\n "
				+ "String main() {return String();}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName /* "$package$"*/; 
//		String mainClassQName = "A"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "sirius.lang.String");

	}
	
	
	@Test
	@DisplayName("access to a member value")
	public void fieldAccessTest() throws Exception {
		String script = "#!\n "
//				+ "class B() {}   "
				+ "class A() {Integer mib = 42;}   "
//				+ "Integer main() {return 42;}";
				+ "Integer main() {A a = A(); return a.mib;}";
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.addFileOutput("/tmp/siriusTmp/module", Optional.of("/tmp/siriusTmp/classes"));

		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName /* "$package$"*/; 
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
//
//		
//		Object result = compileRunAndReturn(script);
		
		assert(result instanceof sirius.lang.Integer);
		assertEquals(((sirius.lang.Integer)result ).value, 42);
//		assertEquals(result, 0);

	}
	
	@Test
	@DisplayName("Check a function can return an Integer local variable")
	public void localVariableTest() throws Exception {
		String script = "#!\n "
		+ "class A(){}\n"
		+ "Integer main() {Integer a = 10; return a;}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
//		backend.addFileOutput("/tmp/siriusTmp/module", Optional.of("/tmp/siriusTmp/classes"));
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "sirius.lang.Integer");
		assertEquals( ((sirius.lang.Integer)result).getValue(), 10);
	}

	@Test
	@DisplayName("A basic member function can be called")
	@Disabled("Restore when scopes are OK (in function call)")
	public void basicMemberFunctionCallTest() throws Exception {
		String script = "#!\n "
		+ "class A(){ Integer add0() {return 42;} }\n"
		+ "Integer main() { A aa = A();  Integer res = aa.add0(); return res;}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.addFileOutput("/tmp/siriusTmp/module", Optional.of("/tmp/siriusTmp/classes"));
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "sirius.lang.Integer");
		assertEquals( ((sirius.lang.Integer)result).getValue(), 42);
	}
}
