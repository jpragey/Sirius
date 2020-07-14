package org.sirius.backend.jvm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.core.TextInputTextProvider;

public class ExpressionStatementTest {

	Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	
	@AfterEach
	public void teardown() {
		assertTrue(reporter.ok());
	}

	// TODO: factorize
	public static ScriptSession compileScript(String sourceCode) {
		
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
		
		FrontEnd frontEnd = new FrontEnd(reporter);
		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
		ScriptSession session = frontEnd.createScriptSession(provider);
		assertTrue(reporter.ok());
		
		return session;
	}

	@Test 
	public void checkFunctionCallIsPresentAsExpressionStatement() {
		ScriptSession session = compileScript("#!\n package p.k; class C(){public void f(){println(\"Hello\");}}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(0);
		assertEquals(pack.getQName().dotSeparated(), "p.k");
		
		
		ClassDeclaration cd = pack.getClasses().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "C"));
		
		AbstractFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "C", "f"));

		assertEquals(func.getBodyStatements().get().size(), 1);
		ExpressionStatement statement = (ExpressionStatement)func.getBodyStatements().get().get(0);
		
		JvmBackend backend = new JvmBackend(reporter /*, Optional.empty()*//* classDir*/ /*, Optional.empty()*/ /* module*/, false /*verboseAst*/
				);
		backend.process(session);
		
	}
}
