package org.sirius.backend.jvm.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.MatcherAssert.;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.*;

import org.hamcrest.core.IsNull;
//import org.hamcrest.;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.common.error.SilentReporter;
import org.sirius.frontend.core.ScriptSession;

public class JvmMainOptionTest {

	private AccumulatingReporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter();
	}
	@AfterEach
	public void tearDown() {
	}


	@Test
	@DisplayName("Find/call a jvmMain entry point, in root module")
	public void generateJvmMainTest() throws Exception {
		String script = "#!\n "
//				+ "module org.mod  \"1.0\" {} "
				+ "class A() {} "
//				+ "A main() {return A();}"
				+ "void jvmMain(){}"
				;
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		BackendOptions backendOptions = new BackendOptions(reporter, Optional.of("jvmMain") /* jvmMain option*/);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, backendOptions);
		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		ClassLoader classLoader = l.getClassLoader();
		
		String mainClassQName = "$package$"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
//		Method main = cls.getMethod("jvmMain", new Class[] { String[].class });
		Method main = cls.getMethod("main", new Class[] { String[].class });
		Object[] args = new Object[] {
				new String[] {}
		};
		
		Object result = main.invoke(null, args);
		assertThat(result, nullValue());
//		assertEquals(result.getClass().getName(), "A");

		assertThat(reporter.ok(), is(true));

	}
	
	
	@Test
	@DisplayName("If a jvmMain entry points specified in the options in not written as bytecode, an error is printed.")
	public void allJvmEntryPointsMustBeWrittenTest() throws Exception {
		String script = "#!\n "
				+ "class A() {} "
				+ "void jvmMain(){}"
				;
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		BackendOptions backendOptions = new BackendOptions(reporter, Optional.of("boom") /* jvmMain option*/);
		JvmBackend backend = new JvmBackend(reporter, false /*verboseAst*/, backendOptions);
//		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
		backend.process(session);
		
		assertThat(reporter.ok(), is(false));
		assertThat(reporter.getErrors().get(0), allOf(containsString("JVM main"), containsString("boom")));
	}
}
