package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class ScriptSessionTest {

	@Test
	public void parsedModuleResultsInModuleDeclaration() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {} ");

		assertEquals(session.getModuleContents().size(), 1);
		assertEquals(session.getModuleContents().get(0).getModuleDeclaration().getqName().toString(), "a.b");
		
	}
	
	@Test(description = "Parsing a script source without module declaration but with a package declaration results in an anonymous module declaration")
	public void packageDeclarationResultsInAnonymousModuleDeclaration() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; ");

		assertEquals(session.getModuleContents().size(), 1);
		assertEquals(session.getModuleContents().get(0).getModuleDeclaration().getqName().toString(), "");
	}
	
	@Test(description = "Parsing a script source without module or package declaration results in an no module declaration")
	public void missingModuleAndPackageDeclarationResultsInNoModuleDeclaration() {
		ScriptSession session = Compiler.compileScript("#!\n");

		assertEquals(session.getModuleContents().size(), 0);
	}
}
