package org.sirius.backend.jvm.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;

public class FunctionTest {
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
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();

		backend.addFileOutput("/tmp/siriusTmp2", Optional.of("jvmTest/classes"));

		backend.process(session);
		
		HashMap<String, Bytecode> map = l.getByteCodesMap();
//		System.out.println(map.keySet());
//		session.getGlobalSymbolTable().dump();

		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);
		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();

//		for(Method m: methods)
//			System.out.println("Method: " + m);

		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
//		System.out.println("Main: " + main);
		
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		System.out.println("Result: " + result);
		
		return result;
		
//		return 43;
	}

	@Test
	public void simpleFunctionCall() throws Exception {
		String script = "#!\n "
//				+ "Integer inc() {return 0;} "
//+ "Integer inc(Integer x) {return x;} "
//+ "Integer inc(Integer x, Integer y) {return 42;} "
+ "Integer id(Integer x) {return x;} "
+ "Integer main() {Integer i= id(43); return i;}"
//+ "Integer main() {Integer i= 43; return i;}"
//				+ "Integer main() {return 43;}"
				;
		
		Object sirResult = compileRunAndReturn(script);

		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertThat(result, is(43));
		
	}

	@Test
	@Disabled
	public void twoArgumentsFunctionCall() throws Exception {
		String script = "#!\n "
//				+ "Integer inc() {return 0;} "
//+ "Integer inc(Integer x) {return x;} "
//+ "Integer inc(Integer x, Integer y) {return 42;} "
+ "Integer id(Integer x, Integer y) {return y;} "
+ "Integer main() {Integer i= id(10,43); return i;}"
//+ "Integer main() {Integer i= 43; return i;}"
//				+ "Integer main() {return 43;}"
				;
		
		Object sirResult = compileRunAndReturn(script);

		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertThat(result, is(43));
		
	}

}
