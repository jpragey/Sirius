package org.sirius.frontend.sdk;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.QNameRefType;
import org.sirius.frontend.symbols.SymbolTableImpl;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.Symbol;


public class SdkToolsTest {
	private Reporter reporter;
	private Scope scope = new Scope();
	private SdkTools sdkTools;
	
	@BeforeEach
	public void setup() throws Exception {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		this.sdkTools = new SdkTools(reporter, scope);
	}
	@AfterEach
	public void tearDown() throws Exception {
		if(this.reporter.hasErrors()) 
			throw new Exception("SDK error, see logs in shell");
	}

	@Test
	public void sdkSLClassesCanBeRetrievedBySimpleName() {
		assertTrue(scope.getSymbolTable().lookupBySimpleName("String").isPresent());
	}
	
	@Test
	public void sdkParsingMustCreateBasicClasses() {
		SymbolTableImpl symbolTable = scope.getSymbolTable();
		assertEquals(reporter.getErrorCount(), 0);
		
		SdkContent sdkContent = sdkTools.getSdkContent();
		
		AstClassDeclaration slIntegerClassDecl = sdkContent.getSiriusLangIntegerASTCD();
		assertThat(slIntegerClassDecl.getQName(), is(new QName("sirius", "lang", "Integer")));

		AstClassDeclaration slBooleanClassDecl = sdkContent.getSiriusLangBooleanASTCD();
		assertThat(slBooleanClassDecl.getQName(), is(new QName("sirius", "lang", "Boolean")));
		
		AstClassDeclaration slFloatClassDecl = sdkContent.getSiriusLangFloatASTCD();
		assertThat(slFloatClassDecl.getQName(), is(new QName("sirius", "lang", "Float")));

		AstClassDeclaration slStringClassDecl = sdkContent.getSiriusLangStringASTCD();
		assertThat(slStringClassDecl.getQName(), is(new QName("sirius", "lang", "String")));

		AstInterfaceDeclaration slFunctionInterfaceDecl = sdkContent.getSiriusLangFunctionASTCD();
		assertThat(slFunctionInterfaceDecl.getQName(), is(new QName("sirius", "lang", "Function")));

		checkSymbolTableContainsInterface(symbolTable, new QName("sirius", "lang", "Stringifiable"));
		checkSymbolTableContainsClass    (symbolTable, new QName("sirius", "lang", "String"));
		checkSymbolTableContainsInterface(symbolTable, new QName("sirius", "lang", "Addable"));
		checkSymbolTableContainsClass	 (symbolTable, new QName("sirius", "lang", "Integer"));
		checkSymbolTableContainsClass	 (symbolTable, new QName("sirius", "lang", "Boolean"));
		checkSymbolTableContainsInterface(symbolTable, new QName("sirius", "lang", "Function"));
		
	}
	
	private void checkSymbolTableContainsClass(SymbolTableImpl symbolTable, QName symbolQName) {
		
		Symbol symbol = symbolTable.lookupByQName(symbolQName).get();
		
		assertSame(symbol.getClassDeclaration().get(), symbol.getClassDeclaration().get());
		AstClassDeclaration stringCD = symbol.getClassDeclaration().get();
	}
	
	private void checkSymbolTableContainsInterface(SymbolTableImpl symbolTable, QName symbolQName) {
		
		Symbol symbol = symbolTable.lookupByQName(symbolQName).get();
		
		assertSame(symbol.getInterfaceDeclaration().get(), symbol.getInterfaceDeclaration().get());
		AstInterfaceDeclaration stringCD = symbol.getInterfaceDeclaration().get();
	}
	
	@Test
	public void checkAncestorsForSiriusInteger() {
		SymbolTableImpl symbolTable = scope.getSymbolTable();
		Symbol symbol = symbolTable.lookupByQName(new QName("sirius", "lang", "Integer")).get();
		AstClassDeclaration cd = symbol.getClassDeclaration().get();  
		
		assertEquals(cd.getAncestors().size(), 2);
		
//		assertEquals(cd.getAncestors().get(0), new QName("sirius", "lang", "Addable"));
		assertEquals(cd.getAncestors().get(0).getText(), "Addable");
		
//		assertEquals(cd.getAncestors().get(1), new QName("sirius", "lang", "Stringifiable"));
		assertEquals(cd.getAncestors().get(1).getText(), "Stringifiable");
	}
	
	@Test
	public void checkBasicTopLevelFunctionsFoundInPackageClass() {
		SymbolTableImpl symbolTable = scope.getSymbolTable();

		Symbol symbol = symbolTable.lookupByQName(new QName("sirius", "lang", "println")).get();
		FunctionDefinition func = symbol.getFunctionDeclaration().get();
		
		assertEquals(func.getqName(), new QName("sirius", "lang", "println"));
//		assertEquals(func.getFormalArguments().size(), 1);
		Partial allArgsPartial = func.getPartials().get(1); 
		assertThat(allArgsPartial.getArgs(), hasSize(1));
		
//		AstFunctionFormalArgument arg0 = func.getFormalArguments().get(0); 
		AstFunctionParameter arg0 = allArgsPartial.getArg(0); 
		assertEquals(arg0.getName().getText(), "text"); // TODO
		assert(arg0.getType() instanceof QNameRefType);
		QNameRefType arg0Type = (QNameRefType)arg0.getType();
		assertEquals(arg0Type.getqName(), new QName("sirius","lang","String"));
		
	}

	@Test
	public void checkIntegerIsStringifiable() {
		SymbolTableImpl symbolTable = scope.getSymbolTable();

		AstClassDeclaration intCD = symbolTable.lookupByQName(new QName("sirius", "lang", "Integer")).get().getClassDeclaration().get();
//		AstClassDeclaration stringifiableCD = symbolTable.lookup(new QName("sirius", "lang", "Stringifiable")).get().getClassDeclaration().get();
		AstInterfaceDeclaration stringifiableCD = symbolTable.lookupByQName(new QName("sirius", "lang", "Stringifiable")).get().getInterfaceDeclaration().get();
		
		assertTrue(stringifiableCD.isAncestorOrSameAs(intCD));
		
	}
	
	
}
