package org.sirius.backend.jvm.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.core.TextInputTextProvider;

public class CompileTools {
	
	public static ScriptSession compileScript(String sourceCode, Reporter reporter) {
		
//		Reporter reporter = new AccumulatingReporter(new ShellReporter());
		
		FrontEnd frontEnd = new FrontEnd(reporter);
		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
		ScriptSession session = frontEnd.createScriptSession(provider);
		assertTrue(reporter.ok());
		
		return session;
	}

}
