package org.sirius.backend.jvm.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.backend.jvm.Util;
import org.sirius.backend.jvm.Utils;
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

	public InMemoryClassWriterListener compileToBytecode(String script) throws Exception {
		
		ScriptSession session =
		CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));

//		backend.addFileOutput("/tmp/siriusTmp2", Optional.of("jvmTest/classes"));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);

		return l;
	}
	
	public Object compileRunAndReturn(String script) throws Exception {

		InMemoryClassWriterListener l = compileToBytecode(script);

		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = Util.jvmPackageClassName /* "$package$"*/; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);
		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();

		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
//		System.out.println("Result: " + result);
		
		return result;
	}

	@Test
	public void simpleFunctionCall() throws Exception {
		String script = "#!\n "
			+ "Integer id(Integer x) {return x;} "
			+ "Integer main() {Integer i= id(43); return i;}"
				;
		
		Object sirResult = compileRunAndReturn(script);

		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertThat(result, is(43));
		
	}

	@Test
	public void twoArgumentsFunctionCall() throws Exception {
		String script = "#!\n "
				+ "Integer add(Integer x, Integer y) {return x+y;} " // -> x+y
				+ "Integer main() {"
				+ "		return add(10,44); "
				+ "}"
				;

		InMemoryClassWriterListener l = compileToBytecode(script);

		HashMap<String, Bytecode> bytecodeMap = l.getByteCodesMap();
		Bytecode bc = bytecodeMap.get(Util.jvmPackageClassName);
		
		Utils.ModuleInfo mi = Utils.parseModuleBytecode(bc.getBytes());
		
		Object sirResult = compileRunAndReturn(script);

		assert(sirResult instanceof sirius.lang.Integer);
		int result = ((sirius.lang.Integer)sirResult).getValue();

		assertThat(result, is(54));
	}

}

class Main {
	public static float caller(Function<Integer, Float> callback) {
		return callback.apply(42);
	}

		public static void main(String[] args) {
	
	
			System.out.println("Hello world");
			
			caller(parm -> 666.0f);

		}
}
