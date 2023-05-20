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
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.QNameRefType;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTableImpl;


public class SdkToolsTest {
	private Reporter reporter;
//	private Scope scope = new Scope("SDK test root");
	private SdkTools sdkTools;
	
	@BeforeEach
	public void setup() throws Exception {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		this.sdkTools = new SdkTools(reporter /*, scope*/);
	}
	@AfterEach
	public void tearDown() throws Exception {
		if(this.reporter.hasErrors()) 
			throw new Exception("SDK error, see logs in shell");
	}

	@Test
	public void sdkSLClassesCanBeRetrievedBySimpleName() {
		assertTrue(this.sdkTools.getScope().getSymbolTable().lookupBySimpleName("String").isPresent());
	}
	
	@Test
	public void sdkParsingMustCreateBasicClasses() {
		SymbolTableImpl symbolTable = this.sdkTools.getScope().getSymbolTable();
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

		checkSymbolTableContainsClass    (symbolTable, new QName("sirius", "lang", "String"));
		checkSymbolTableContainsClass	 (symbolTable, new QName("sirius", "lang", "Integer"));
		checkSymbolTableContainsClass	 (symbolTable, new QName("sirius", "lang", "Boolean"));
	}
	
	private void checkSymbolTableContainsClass(SymbolTableImpl symbolTable, QName symbolQName) {
		
		Symbol symbol = symbolTable.lookupByQName(symbolQName).get();
		
		assertSame(symbol.getClassDeclaration().get(), symbol.getClassDeclaration().get());
		AstClassDeclaration stringCD = symbol.getClassDeclaration().get();
	}
	
	@Test
	public void checkAncestorsForSiriusInteger() {
		SymbolTableImpl symbolTable = this.sdkTools.getScope().getSymbolTable();
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
		SymbolTableImpl symbolTable = this.sdkTools.getScope().getSymbolTable();

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
}
