package org.sirius.frontend.parser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.core.ModuleContent;
import org.sirius.frontend.core.ScriptSession;
import org.testng.annotations.Test;

public class CompilationUnitTest {

	@Test(enabled = true)
	void testCUcontainsShebang() {
		
		ScriptSession session = Compiler.compileScript("#!/bin.bash");
//		List<ModuleContent> cu = Compiler.compileScript("#!/bin.bash");
		Optional<ShebangDeclaration> shebang = session.getShebang();
		assertTrue(shebang.isPresent());
	}
	
	@Test(enabled = true)
	void testCUcontainsTopLevelFunctions() {
		String source = "#!\n void ff(){} void gg () {} void hh () {}";
		ScriptSession session = Compiler.compileScript(source);
		
//		List<ModuleContent> modules = session
//				.getModuleContents();
		List<ModuleDeclaration> modules = session
				.getModuleDeclarations();
		assertEquals(modules.size(), 1);
				
		List<PackageDeclaration> packageDeclarations = modules
				.get(0)
				.getPackages();
		assertEquals(packageDeclarations.size(), 1);
				
//		List<AstFunctionDeclaration> fds = packageDeclarations
//				.get(0)
//				.getFunctionDeclarations();
		List<TopLevelFunction> fds = packageDeclarations
				.get(0)
				.getFunctions();
		
//		AstPackageDeclaration pd = packageDeclarations.get(0);
		
		
		assertEquals(fds.size(), 3);
		assertEquals(fds.get(0).getQName().getLast(), "ff");
		assertEquals(fds.get(1).getQName().getLast(), "gg");
		assertEquals(fds.get(2).getQName().getLast(), "hh");
	}
	
	@Test(enabled = false)
	void testDummyTopLevelFunctionsReturnsString() {
//		
//		ModuleContent md = Compiler.compile("ff(){ return \"hello\";}");
//		CompilationUnit cu = md.getCompilationUnits().get(0);
//		List<FunctionDeclaration> fds = cu.getFunctionDeclarations();
//		
//		assertEquals(fds.size(), 1);
//		FunctionDeclaration fd = fds.get(0);
//		assertNotNull(fd);
//
//		Statement stmt = fd.getStatements().get(0);
//		assertNotNull(stmt);
//		assert(stmt instanceof ReturnStatement);
//		ReturnStatement returnStatement = (ReturnStatement) stmt;
//		
//		StringConstantExpression expr =  (StringConstantExpression)returnStatement.getExpression();
//		assertEquals(expr.getContent().getText(), "\"hello\"");
//
//		// ---- Test root class exists
//		List<ClassDeclaration> cds = cu.getClassDeclarations();
//		assertEquals(cds.size(), 1);
//		ClassDeclaration rootClass = cds.get(0);
//		FunctionDeclaration ffFunction = rootClass.getFunctionDeclarations().get(0);
//		
//		assertEquals(ffFunction.getName().getText(), "ff");
	}
}
