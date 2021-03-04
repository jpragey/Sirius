package org.sirius.backend.jvm.integration;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.FailFastReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class TopLevelFunctionCall {

	private Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new FailFastReporter(new AccumulatingReporter(new ShellReporter()));
		
	}
	
	@AfterEach
	public void teardown() {
	}
	

	@Test
	public void callTopLevelFunctionTest() throws Exception {
		
		String script = "#!\n "
				+ "String getVal() {return \"Hello\";}"
//				+ "void main() {println(getVal());}"
//+ "void main() {println(\"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\");}"
+ "void main() {println(\"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\");}"
				;
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		HashMap<String, Bytecode> map = l.getByteCodesMap();
		System.out.println(map.keySet());

		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);
		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();

		for(Method m: methods)
			System.out.println("Method: " + m);

		Method main = cls.getMethod("main", new Class[] {
				//String[].class
		});
		System.out.println("Main: " + main);
		
		Object[] argTypes = new Object[] {
				//new String[]{""}
		};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		System.out.println("Result: " + result);
		//		return result;
		
	}

	@Test
//	@Disabled("temp.")
	public void callUserDefinedFunctionTest() throws Exception {
		
		String script = "#!\n "
//				+ "String getVal() {return \"Hello\";}"
//+ "Integer add(Integer x, Integer y) {return x;}"
+ "Integer add(Integer x, Integer y) {return x+y;}"
				//				+ "void main() {println(getVal());}"
//				+ "Integer main() {Integer r = add(); return r;}"
//				+ "Integer main() {Integer r = 42; return 43;}"
//+ "Integer main() {return add(40);}"
+ "Integer main() {Integer i = add(40, 41); return i;}"
				;
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();

		backend.addFileOutput("/tmp/siriusTmp/module", Optional.of("/tmp/siriusTmp/classes"));
		
		backend.process(session);
		
		HashMap<String, Bytecode> map = l.getByteCodesMap();
		System.out.println(map.keySet());

		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);
		Object helloObj = cls.getDeclaredConstructor().newInstance();

//		for(Method m: helloObj.getClass().getDeclaredMethods())
//			System.out.println("Method: " + m);

		Method main = cls.getMethod("main", new Class[] {});
		System.out.println("Main: " + main);
		
		sirius.lang.Integer result = (sirius.lang.Integer)main.invoke(null, new Object[] {}/*args*/);
		System.out.println("Result: " + result.getValue());
		assertThat(result.getValue(), is(81));
		
	}

}
