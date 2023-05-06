package org.sirius.backend.jvm.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

public class IfElseStatementTest {

	private Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void teardown() {
		assertTrue(reporter.ok());
	}


	int runIfThenTest(String ifThenExpr) throws Exception {
		String script = "#!\n "
				+ "Integer main() {" + ifThenExpr + " return 100;}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
//		String mainClassQName = Util.topLevelClassName; 
		String mainClassQName = Util.jvmPackageClassQName.dotSeparated() /* "$package$"*/; 

		Class<?> cls = classLoader.loadClass(mainClassQName);

		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
		Object[] argTypes = new Object[] {};
		
		Object result = main.invoke(null, argTypes /*, args*/);
		
		assertThat(result.getClass().getName(), is("sirius.lang.Integer"));
		sirius.lang.Integer intResult = (sirius.lang.Integer) result;
		return intResult.value;
		
	}

	@Test
	public void ifThenEvaluatingFalseTest() throws Exception {
		assertThat(runIfThenTest("if(false) return 42;"), is(100));
	}
	@Test
	public void ifThenEvaluatingTrueTest() throws Exception {
		assertThat(runIfThenTest("if(true) return 42;"), is(42));
	}
	
	
	@Test
	public void ifThenElseEvaluatingFalseTest() throws Exception {
		assertThat(runIfThenTest("if(false) return 42; else return 43;"), is(43));
	}
	@Test
	public void ifThenElseEvaluatingTrueTest() throws Exception {
		assertThat(runIfThenTest("if(true) return 42; else return 43;"), is(42));
	}

}
