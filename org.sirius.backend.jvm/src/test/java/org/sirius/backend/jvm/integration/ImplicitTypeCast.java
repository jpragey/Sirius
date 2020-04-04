package org.sirius.backend.jvm.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ImplicitTypeCast {

	private Reporter reporter;

	@BeforeMethod
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}
	
	@AfterMethod
	public void teardown() {
		assertTrue(reporter.ok());
	}
	

	public Object compileRunAndReturn(String script) throws Exception {
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		HashMap<String, Bytecode> map = l.getByteCodesMap();
		System.out.println(map.keySet());
		session.getGlobalSymbolTable().dump();

		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);
		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();

		for(Method m: methods)
			System.out.println("Method: " + m);

		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
//		System.out.println("Main: " + main);
		
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		System.out.println("Result: " + result);
		
		return result;
	}

	
	
	@Test(enabled = true)
	public void returnConstantInt() throws Exception {

		String script = "#!\n "
				+ "import a.b {}"
				+ "Integer main() {return 42;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();
		
		assertEquals(result, 42);
	}
	
	@Test(enabled = true)
	public void returnIntSum() throws Exception {
		String script = "#!\n Integer main() {return 42 + 43;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertEquals(result, 85);
	}
	
	@Test(enabled = true)
	public void returnIntSubstraction() throws Exception {
		String script = "#!\n Integer main() {return 42 - 43;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertEquals(result, -1);
	}
	
	@Test(enabled = true)
	public void returnIntMult() throws Exception {
		String script = "#!\n Integer main() {return 10 * 11;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();
		
		assertEquals(result, 110);
	}
	
	@Test(enabled = true)
	public void returnIntDiv() throws Exception {
		String script = "#!\n Integer main() {return 100 / 3;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();
		
		assertEquals(result, 33);
	}
	
	@Test(enabled = true)
	public void returnConstantString() throws Exception {

		String script = "#!\n "
				+ "import a.b {}"
				+ "void main() {println(\"42\");}";
		Object result = compileRunAndReturn(script);

		assertEquals(result, null);
	}
}
