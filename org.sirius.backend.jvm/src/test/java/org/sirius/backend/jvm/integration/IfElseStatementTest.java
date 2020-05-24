package org.sirius.backend.jvm.integration;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class IfElseStatementTest {

	private Reporter reporter;

	@BeforeMethod
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}

	int runIfThenTest(String ifThenExpr) throws Exception {
		String script = "#!\n "
				+ "Integer main() {" + ifThenExpr + " return 100;}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertEquals(result.getClass().getName(), "sirius.lang.Integer");
		sirius.lang.Integer intResult = (sirius.lang.Integer) result;
		return intResult.value;
		
	}

	@Test(description = "")
	public void ifThenEvaluatingFalseTest() throws Exception {
		assertEquals(runIfThenTest("if(false) return 42;"), 100);
	}
	@Test(description = "")
	public void ifThenEvaluatingTrueTest() throws Exception {
		assertEquals(runIfThenTest("if(true) return 42;"), 42);
	}
	
	
	@Test(description = "")
	public void ifThenElseEvaluatingFalseTest() throws Exception {
		assertEquals(runIfThenTest("if(false) return 42; else return 43;"), 43);
	}
	@Test(description = "")
	public void ifThenElseEvaluatingTrueTest() throws Exception {
		assertEquals(runIfThenTest("if(true) return 42; else return 43;"), 42);
	}

}
