package org.sirius.frontend.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;
import org.sirius.frontend.core.ModuleContent;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.core.StandardSession;
import org.sirius.frontend.core.TextInputTextProvider;

public class Compiler {


	public static ScriptSession compileScript(String sourceCode) {
		
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
		
		FrontEnd frontEnd = new FrontEnd(reporter);
		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
		ScriptSession session = frontEnd.createScriptSession(provider);
		assertTrue(reporter.ok());
		
		return session;
	}

	/** Check if reporter has errors. 
	 * If reporter has error(s), print them to stderr ans assert.
	 * 
	 */
	public static void assertReporter(AccumulatingReporter reporter) {
		if(reporter.hasErrors()) {
			System.err.println("Reporter has " + reporter.getErrorCount() + " errors:");
			for(String err: reporter.getErrors()) {
				System.err.println(err);
			}
			assertEquals(reporter.getErrorCount(), 0, "Reporter has " + reporter.getErrorCount() + " errors. See error log.");
		}
	}
	
	public static List<ModuleDeclaration> compileStandard(InputTextProvider... providers) {
		
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter());
		
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(providers));
		
		assertReporter(reporter);
		
		List<ModuleDeclaration> cus = session.getModuleDeclarations();
		
		assertTrue(reporter.ok());
		
		return cus;
		
	}

}
