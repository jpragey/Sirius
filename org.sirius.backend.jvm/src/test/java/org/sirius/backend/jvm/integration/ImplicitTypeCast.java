package org.sirius.backend.jvm.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.backend.jvm.Util;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;

public class ImplicitTypeCast {

	private Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}
	
	@AfterEach
	public void teardown() {
		assertTrue(reporter.ok());
	}
	

	public Object compileRunAndReturn(String script) throws Exception {
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		HashMap<String, Bytecode> map = l.getByteCodesMap();
		System.out.println(map.keySet());
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName /* "$package$"*/; 
		
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

	
	
	@Test
	public void returnConstantInt() throws Exception {

		String script = "#!\n "
				+ "import a.b {}"
				+ "Integer main() {return 42;}";
//		+ "Integer main() {return 42;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();
		
		assertThat(result, is(42));
	}
	
	@Test
	public void returnIntSum() throws Exception {
		String script = "#!\n Integer main() {return 42 + 43;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertThat(result, is(85));
	}
	
	@Test
	public void returnIntSubstraction() throws Exception {
		String script = "#!\n Integer main() {return 42 - 43;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertThat(result, is(-1));
	}
	
	@Test
	public void returnIntMult() throws Exception {
		String script = "#!\n Integer main() {return 10 * 11;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();
		
		assertThat(result, is(110));
	}
	
	@Test
	public void returnIntDiv() throws Exception {
		String script = "#!\n Integer main() {return 100 / 3;}";
		Object sirResult = compileRunAndReturn(script);
		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();
		
		assertThat(result, is(33));
	}
	
	@Test
	public void returnConstantString() throws Exception {

		String script = "#!\n "
				+ "import a.b {}"
				+ "void main() {println(\"42\");}";
//				+ "void main() {println0();}";
		Object result = compileRunAndReturn(script);

		assertThat(result, nullValue());
	}
}
