package org.sirius.frontend.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.FunctionBody;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;

public class ScopeTest {

	private Reporter reporter;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}
	
	@AfterEach
	public void teardown() {
		assertTrue(reporter.ok());
	}

	public static AstModuleDeclaration getModule(int moduleIndex, ScriptSession session, Consumer<AstModuleDeclaration> check) {
		AstModuleDeclaration astModule = session.getAstModules().get(moduleIndex);
		check.accept(astModule);
		
		return astModule;
	}
	public static AstPackageDeclaration getPackage(int packageIndex, AstModuleDeclaration moduleDecl, Consumer<AstPackageDeclaration> check) {
		AstPackageDeclaration astPackage = moduleDecl.getPackageDeclarations().get(packageIndex);
		check.accept(astPackage);
		return astPackage;
	}
	
	@Test
	public void checkFunctionArgeScopes() {
		String sourceCode = "#!\n "
			+ "Integer id(Integer x, Integer y) {return x;} "
//			+ "Integer main() {Integer i= id(43); return i;}"
			;

		FrontEnd frontEnd = new FrontEnd(reporter);
		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
		ScriptSession session = frontEnd.createScriptSession(provider);
		assertTrue(reporter.ok());
		
//		AstModuleDeclaration astModule = session.getAstModules().get(0);
//		assertThat(astModule.getModuleDeclaration().getQNameString(), equalTo(""));
//		assertThat(astModule.getQnameString(), equalTo(""));

		AstModuleDeclaration astModule = getModule(0, session, decl -> {assertThat(decl.getQnameString(), equalTo(""));});
		AstPackageDeclaration astPackage = getPackage(0, astModule, decl -> {assertThat(decl.getQnameString(), equalTo(""));});

		assertThat(astPackage.getClassDeclarations().size(), equalTo(0));	// Not really useful

		assertThat(astPackage.getFunctionDeclarations().size(), equalTo(1));
		FunctionDefinition idPartials = astPackage.getFunctionDeclarations().get(0);
		assertThat(idPartials.getNameString(), equalTo("id"));
		
		Partial id_0 = idPartials.byArgCount(0).get();
		assertEquals(id_0.getArgs().size(), 0);
		
		// Check partial scope(s) are Contain function arguments
		Partial id_1 = idPartials.byArgCount(1).get();
		assertEquals(id_1.getArgs().size(), 1);
		assertTrue(id_1.getScope().getFunctionParameter("x").isPresent());
//		assertTrue(idPartials.byArgCount(2).isEmpty());
		
		Partial id_2 = idPartials.byArgCount(2).get();
		assertEquals(id_2.getArgs().size(), 2);
		assertTrue(id_2.getScope().getFunctionParameter("x").isPresent());
		assertTrue(id_2.getScope().getFunctionParameter("y").isPresent());
		assertTrue(idPartials.byArgCount(3).isEmpty());
		
//		
//		AstPackageDeclaration astPackage = astModule.getPackageDeclarations().get(0);
//		assertThat(astPackage.getQnameString(), equalTo(""));

	
	}
	@Test
	public void checkReturnAnArgFoundInParameters() {
		String sourceCode = "#!\n "
			+ "Integer id(Integer x, Integer y) {return x;} "
//			+ "Integer main() {Integer i= id(43); return i;}"
			;

		FrontEnd frontEnd = new FrontEnd(reporter);
		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
		ScriptSession session = frontEnd.createScriptSession(provider);
		assertTrue(reporter.ok());
		
		AstModuleDeclaration astModule = getModule(0, session, decl -> {assertThat(decl.getQnameString(), equalTo(""));});
		AstPackageDeclaration astPackage = getPackage(0, astModule, decl -> {assertThat(decl.getQnameString(), equalTo(""));});

		assertThat(astPackage.getClassDeclarations().size(), equalTo(0));	// Not really useful

		assertThat(astPackage.getFunctionDeclarations().size(), equalTo(1));
		FunctionDefinition idPartials = astPackage.getFunctionDeclarations().get(0);
		assertThat(idPartials.getNameString(), equalTo("id"));
		
		Partial id_2 = idPartials.byArgCount(2).get();
		assertEquals(id_2.getArgs().size(), 2);
		assertTrue(id_2.getScope().getFunctionParameter("x").isPresent());
		assertTrue(id_2.getScope().getFunctionParameter("y").isPresent());
//		assertTrue(idPartials.byArgCount(3).isEmpty());
		
		FunctionBody body = idPartials.getBody();
//		AstReturnStatement returnStmt = (AstReturnStatement)body.get(0);
		AstReturnStatement returnStmt = (AstReturnStatement)body.getStatement(0);
		ReturnStatement rs = (ReturnStatement)returnStmt.toAPI().get();
//		
//		AstPackageDeclaration astPackage = astModule.getPackageDeclarations().get(0);
//		assertThat(astPackage.getQnameString(), equalTo(""));

	
	}
}
