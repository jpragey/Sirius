package org.sirius.frontend.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.ast.AstBlock;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstVisitor;
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
		assertTrue(id_1.getScope().getFunctionArgument("x").isPresent());
//		assertTrue(idPartials.byArgCount(2).isEmpty());
		
		Partial id_2 = idPartials.byArgCount(2).get();
		assertEquals(id_2.getArgs().size(), 2);
		assertTrue(id_2.getScope().getFunctionArgument("x").isPresent());
		assertTrue(id_2.getScope().getFunctionArgument("y").isPresent());
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
		assertTrue(id_2.getScope().getFunctionArgument("x").isPresent());
		assertTrue(id_2.getScope().getFunctionArgument("y").isPresent());
//		assertTrue(idPartials.byArgCount(3).isEmpty());
		
		FunctionBody body = idPartials.getBody();
//		AstReturnStatement returnStmt = (AstReturnStatement)body.get(0);
		AstReturnStatement returnStmt = (AstReturnStatement)body.getStatement(0);
		ReturnStatement rs = (ReturnStatement)returnStmt.toAPI().get();
//		
//		AstPackageDeclaration astPackage = astModule.getPackageDeclarations().get(0);
//		assertThat(astPackage.getQnameString(), equalTo(""));

	
	}
	
	private ScriptSession compile(String sourceCode) {

			FrontEnd frontEnd = new FrontEnd(reporter);
			TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
			ScriptSession session = frontEnd.createScriptSession(provider);
			assertTrue(reporter.ok());
			return session;
	}
	
	@Test
	public void checkScopesNames() {
		String sourceCode = "#!\n "
				+ "module m0.m1 \"1.0\" {}"
				+ "package pkg0.pkg1;"
			+ "Integer id(Integer x, Integer y) { {{{{{{{}{}}}}}}} {{}}{{}}{{}}  return x;} "
//			+ "Integer main() {Integer i= id(43); return i;}"
			;
		ScriptSession session = compile(sourceCode);
//
//		FrontEnd frontEnd = new FrontEnd(reporter);
//		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
//		ScriptSession session = frontEnd.createScriptSession(provider);
//		assertTrue(reporter.ok());
		HashMap<String, AstClassDeclaration> cdMap = new HashMap<>(); 
		HashMap<String, AstPackageDeclaration> pdMap = new HashMap<>(); 
		HashMap<String, FunctionDefinition> fdMap = new HashMap<>(); 
		
		session.applyVisitors(reporter, session.getCompilationUnit(), new AstVisitor() {
			@Override
			public void startModuleDeclaration(AstModuleDeclaration declaration) {
				declaration.getqName();
				// TODO Auto-generated method stub
				AstVisitor.super.startModuleDeclaration(declaration);
			}
			@Override
			public void startClassDeclaration(AstClassDeclaration cd) {
//				System.out.println("AstClassDeclaration " + cd.getQName() + ": " + cd.getSymbolTable().getDbgName());
				cdMap.put(cd.getSymbolTable().getDbgName(), cd);
			}
			@Override
			public void startPackageDeclaration(AstPackageDeclaration pkgDeclaration) {
//				System.out.println("Package '" + pkgDeclaration.getQname() + "': '" + pkgDeclaration.getSymbolTable().getDbgName() + "'");
				pdMap.put(pkgDeclaration.getSymbolTable().getDbgName(), pkgDeclaration);
			}
			@Override
			public void startFunctionDefinition(FunctionDefinition fd) { // TODO
//				System.out.println("FunctionDefinition " + fd.getqName() + ": " + fd.getSymbolTable().getDbgName());
//				pdMap.put(fd.getymbolTable().getDbgName(), fd);
				}
			@Override
			public void startPartial(Partial fd) {
//				System.out.println("FunctionDefinition " + fd.getqName() + ": " + fd.getSymbolTable().getDbgName());
			}
			@Override
			public void startBlock(AstBlock block) {
//				System.out.println("Block : " + block.getSymbolTable().getDbgName());
			}
		});
		
	}
		
}
