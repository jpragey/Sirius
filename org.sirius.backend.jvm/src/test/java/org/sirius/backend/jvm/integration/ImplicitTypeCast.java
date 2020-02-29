package org.sirius.backend.jvm.integration;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.sirius.backend.core.Backend;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.core.TextInputTextProvider;
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
	
//	// TODO: factorise
//	public static ScriptSession compileScript(String sourceCode) {
//		
//		Reporter reporter = new AccumulatingReporter(new ShellReporter());
//		
//		FrontEnd frontEnd = new FrontEnd(reporter);
//		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
//		ScriptSession session = frontEnd.createScriptSession(provider);
//		assertTrue(reporter.ok());
//		
//		return session;
//	}

	
	@Test
	public void runHelloWorld() throws Exception {
		
		String script = "#!\n "
//				+ "String getVal() {return \"Hello\";}"
				+ "void main(String[] args) {println(\"42\");}";
//		+ "void main(String args, String[] args2) {println(\"42\");}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
//		ScriptSession session = compileScript("#!\n void main(String s) {println(42);}");
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
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
				String[].class
		});
		System.out.println("Main: " + main);
		
		Object[] argTypes = new Object[] {
				new String[]{""}
		};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		System.out.println("Result: " + result);
		//		return result;
		
	}
}
