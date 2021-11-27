package org.sirius.frontend.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.core.ScriptSession;

public class CompilationUnitTest {

	@Test
	public void testCUcontainsShebang() {
		
		ScriptSession session = Compiler.compileScript("#!/bin.bash");
		Optional<ShebangDeclaration> shebang = session.getShebang();
		assertTrue(shebang.isPresent());
	}
	
	@Test
	public void testCUcontainsTopLevelFunctions() {
		String source = "#!\n void ff(){} void gg () {} void hh () {}";
		ScriptSession session = Compiler.compileScript(source);
		
		// -- AST
		List<AstModuleDeclaration> astModules = session.getAstModules();
		assertEquals(astModules.size(), 1);
		
		List<AstPackageDeclaration> astPackageDeclarations = astModules.get(0).getPackageDeclarations();
		assertEquals(astPackageDeclarations.size(), 1);

		List<FunctionDefinition> partialLists = astPackageDeclarations.get(0).getFunctionDeclarations();
		assertEquals(partialLists.size(), 3);
		
		// -- API
		List<ModuleDeclaration> modules = session.getModuleDeclarations();
		assertEquals(modules.size(), 1);
				
		List<PackageDeclaration> packageDeclarations = modules.get(0).packageDeclarations();
		assertEquals(packageDeclarations.size(), 1);
				
		List<AbstractFunction> fds = packageDeclarations.get(0).getFunctions();
		
		assertEquals(fds.size(), 3);
		assertEquals(fds.get(0).qName().getLast(), "ff");
		assertEquals(fds.get(1).qName().getLast(), "gg");
		assertEquals(fds.get(2).qName().getLast(), "hh");
	}
	
	@Test
	public void testDummyTopLevelFunctionsReturnsString() {
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
