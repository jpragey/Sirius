package org.sirius.frontend.parser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

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
	
	@Test(enabled = false)
	void testCUcontainsTopLevelFunctions() {
		String source = "#!\n Void ff(){} Void gg () {}";
		ScriptSession session = Compiler.compileScript(source);
		
		List<ModuleContent> modules = session
				.getModuleContents();
		assertEquals(modules.size(), 1);
				
		List<AstPackageDeclaration> packageDeclarations = modules
				.get(0)
				.getPackageContents();
		assertEquals(packageDeclarations.size(), 1);
				
		List<AstFunctionDeclaration> fds = packageDeclarations
				.get(0)
				.getFunctionDeclarations();
		
		AstPackageDeclaration pd = packageDeclarations.get(0);
		
		
		assertEquals(fds.size(), 2);
		assertEquals(fds.get(0).getName().getText(), "ff");
		assertEquals(fds.get(1).getName().getText(), "gg");
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
