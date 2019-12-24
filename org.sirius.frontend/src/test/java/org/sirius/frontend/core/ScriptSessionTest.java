package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
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
	
	@Test 
	public void checkQNameAreSetInClassAndFunctions() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){}}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(0);
		assertEquals(pack.getQName().dotSeparated(), "p.k");
		
		
		ClassDeclaration cd = pack.getClasses().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "C"));
		
		MemberFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "C", "f"));
		
	}
	
}
