package org.sirius.frontend.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.MatcherAssert.h;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.ast.AstBlock;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.FunctionBody;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

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

		AstModuleDeclaration astModule = getModule(0, session, decl -> {assertThat(decl.getQnameString(), equalTo("sirius.default"));});
		AstPackageDeclaration astPackage = getPackage(0, astModule, decl -> {assertThat(decl.getQnameString(), equalTo("sirius.default"));});

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
		
		AstModuleDeclaration astModule = getModule(0, session, 
				decl -> {assertThat(decl.getQnameString(), equalTo("sirius.default"));});
		AstPackageDeclaration astPackage = getPackage(0, astModule, 
				decl -> {assertThat(decl.getQnameString(), equalTo("sirius.default"));});

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
	

//	private void compile(String sourceCode) {
//		FrontEnd frontEnd = new FrontEnd(reporter);
//		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
//		ScriptSession session = frontEnd.createScriptSession(provider);
//		assertTrue(reporter.ok());
//
//	}
	@Test
	@DisplayName("Check that the body of a top-level function sees other top-level functions")
//	@Disabled("FunctionDefinition doesn't have a scope yet")	// TODO
	public void moduleScopeSeenIntopLevelFunction() {
		String source = "void before(){}" +
						"void func(){}" +
						"void after(){}"
						;
		
		ScriptSession session = compile(source);
		
		AstPackageDeclaration pd = session.getAstModules().get(0).getPackageDeclarations().get(0);
		List<FunctionDefinition> fds = pd.getFunctionDeclarations();
		assertThat(fds.size(), is(3));

		FunctionDefinition beforeFD = fds.get(0);
		FunctionDefinition funcFD = fds.get(1);
		FunctionDefinition afterFD = fds.get(2);
		
//		SymbolTable funcSymbolTable = funcFD.getSymbolTable();
		assertThat(funcFD.getScope().lookupSymbol("func").isPresent(), is(true));
		assertThat(funcFD.getScope().lookupSymbol("before").isPresent(), is(true));
		assertThat(funcFD.getScope().lookupSymbol("after").isPresent(), is(true));
		
	}

	@Test
	@DisplayName("Nested blocks: inner block parent ST is outer block ST")
//	@Disabled("FunctionDefinition doesn't have a scope yet")	// TODO
	public void innerParentIsOuterST() {
		String source = "void func(){ {} }"
						;
		
 		ScriptSession session = compile(source);
		
		AstPackageDeclaration pd = session.getAstModules().get(0).getPackageDeclarations().get(0);
		List<FunctionDefinition> fds = pd.getFunctionDeclarations();
		assertThat(fds.size(), is(1));

		FunctionDefinition funcFD = fds.get(0);
		
		Scope funcFDScope = funcFD.getScope();
		assertThat(funcFDScope, notNullValue());

		// parent -> Package scope
		assertThat(funcFDScope.getParentScope().isPresent(), is(true)); 
		Scope funcParentScope = funcFDScope.getParentScope().get();

		// parent parent -> Module scope
		assertThat(funcParentScope.getParentScope().isPresent(), is(true)); 
		Scope moduleScope = funcParentScope.getParentScope().get();

//		SymbolTable moduleST = moduleScope.getSymbolTable();
//		Optional<Symbol> optPrintlnFroModuleSymbol = moduleST.lookupBySimpleName("println");
		Optional<Symbol> optPrintlnFroModuleSymbol = moduleScope.lookupSymbol("println");
		assertThat(optPrintlnFroModuleSymbol.isPresent(), is(true));

		
		SymbolTable funcSymbolTable = funcFD.getSymbolTable();
//		
//		Optional<Symbol> optPrintlnSymbol =  funcSymbolTable.lookupBySimpleName("println");
		Optional<Symbol> optPrintlnSymbol =  funcFD.getScope().lookupSymbol("println");
		assertThat(optPrintlnSymbol.isPresent(), is(true));
		
	}

	@Test
	@DisplayName("A class instance can see its member(s)")
//	@Disabled("FunctionDefinition doesn't have a scope yet")	// TODO
	public void classInstanceHasItsMembersInScope() {
		String source = "class A(){ Integer add0() {return 42;} }\n"
				+ "Integer main() { A aa = A();  Integer res = aa.add0(); return res;}";
		
 		ScriptSession session = compile(source);
 		AstPackageDeclaration pkg = session.getAstModules().get(0).getPackageDeclarations().get(0);
 		
 		FunctionDefinition mainFct = pkg.getFunctionDeclarations().get(0);
 		assertThat(mainFct.getNameString(), is("main"));
 		
 		Optional<Symbol> addOptSymbol = mainFct.getScope().lookupSymbol("add0");
 		assertThat(addOptSymbol.isPresent(), is(false)); // Not directly accessed

 		assertThat(mainFct.getBody().getStatement(1), instanceOf(AstLocalVariableStatement.class)); // res = aa.add0()
 		AstLocalVariableStatement resAssignStatement = (AstLocalVariableStatement)mainFct.getBody().getStatement(1);

 		assertThat(resAssignStatement.getInitialValue().get(), instanceOf(AstFunctionCallExpression.class));
 		AstFunctionCallExpression add0CallExpr = (AstFunctionCallExpression)resAssignStatement.getInitialValue().get();

 		assertThat(add0CallExpr.getThisExpression().get(), instanceOf(SimpleReferenceExpression.class));
 		SimpleReferenceExpression aaRefExpr = (SimpleReferenceExpression)add0CallExpr.getThisExpression().get();
	}
}
