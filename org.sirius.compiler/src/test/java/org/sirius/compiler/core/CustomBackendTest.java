package org.sirius.compiler.core;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.backend.core.Backend;
import org.sirius.common.error.FailFastReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.compiler.options.CompileOptionsValues;
import org.sirius.compiler.options.RootOptionValues;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.core.TextInputTextProvider;

public class CustomBackendTest {

	public static class CustomBackend implements Backend {
		public boolean processCalled = false;
		@Override
		public String getBackendId() {
			return "org.jpr.custom.dummy";
		}

		@Override
		public void process(Session moduleContent) {
			System.out.println("Custom backend run on " + moduleContent);
			processCalled = true;
		}
		
	}
	
	@Test
	@DisplayName("Compiling a basic source to a custom backend must not cause error")
	public void customBackendCompilationSmokeTest() {
		Reporter reporter =  new FailFastReporter(new ShellReporter());
		
		CompilerTool compilerTool = new CompilerTool(reporter, 
				new RootOptionValues(reporter), 
				new CompileOptionsValues.Builder()
					.addSource(" module dummyModule \"0.1\" {}")
					.get(), 
				(source -> new TextInputTextProvider("" /*packagePhysicalName*/, "" /*resourcePhysicalName*/, source)));
		
		CustomBackend customBackend = new CustomBackend();
		
		compilerTool.runCompileTool(List.of(customBackend));
		
		assertThat(customBackend.processCalled, is(true));
		
	}
}
