package org.sirius.frontend.sdk;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.QNameRefType;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SdkToolsTest {
	private Reporter reporter;
	private DefaultSymbolTable symbolTable ;
	private SdkTools sdkTools;
	
	@BeforeMethod
	public void setup() throws Exception {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		this.sdkTools = new SdkTools(reporter);
		symbolTable = new DefaultSymbolTable();
		sdkTools.parseSdk(symbolTable);
		
		if(this.reporter.hasErrors()) 
			throw new Exception("SDK error, see logs in shell");
		
	}

	@Test
	public void sdkSLClassesCanBeRetrievedBySimpleName() {
		assertTrue(symbolTable.lookup("String").isPresent());
		
		
		
	}
	@Test
	public void sdkParsingMustCreateBasicClasses() {
		assertEquals(reporter.getErrorCount(), 0);
		
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "Stringifiable"));
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "String"));
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "Addable"));
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "Integer"));
		
	}
	
	private void checkSymbolTableContains(DefaultSymbolTable symbolTable, QName symbolQName) {
		
		Symbol symbol = symbolTable.lookup(symbolQName).get();
//		Symbol simpleSymbol = symbolTable.lookup(new QName(simpleName)).get();
		
		assertSame(symbol.getClassDeclaration().get(), symbol.getClassDeclaration().get());
		AstClassDeclaration stringCD = symbol.getClassDeclaration().get();
	}
	
	@Test
	public void checkAncestorsForSiriusInteger() {
		Symbol symbol = symbolTable.lookup(new QName("sirius", "lang", "Integer")).get();
		AstClassDeclaration cd = symbol.getClassDeclaration().get();  
		
		assertEquals(cd.getAncestors().size(), 2);
		
//		assertEquals(cd.getAncestors().get(0), new QName("sirius", "lang", "Addable"));
		assertEquals(cd.getAncestors().get(0).getSimpleName().getText(), "Addable");
		
//		assertEquals(cd.getAncestors().get(1), new QName("sirius", "lang", "Stringifiable"));
		assertEquals(cd.getAncestors().get(1).getSimpleName().getText(), "Stringifiable");
	}
	
	@Test
	public void checkBasicTopLevelFunctionsFoundInPackageClass() {
		
		Symbol symbol = symbolTable.lookup(new QName("sirius", "lang", "println")).get();
		AstFunctionDeclaration func = symbol.getFunctionDeclaration().get();
		
		assertEquals(func.getQName(), new QName("sirius", "lang", "println"));
		assertEquals(func.getFormalArguments().size(), 1);
		
		AstFunctionFormalArgument arg0 = func.getFormalArguments().get(0); 
		assertEquals(arg0.getName().getText(), "text"); // TODO
		assert(arg0.getType() instanceof QNameRefType);
		QNameRefType arg0Type = (QNameRefType)arg0.getType();
		assertEquals(arg0Type.getqName(), new QName("sirius","lang","String"));
		
	}

	@Test
	public void checkIntegerIsStringifiable() {
		
		AstClassDeclaration intCD = symbolTable.lookup(new QName("sirius", "lang", "Integer")).get().getClassDeclaration().get();
		AstClassDeclaration stringifiableCD = symbolTable.lookup(new QName("sirius", "lang", "Stringifiable")).get().getClassDeclaration().get();
		
		assertTrue(stringifiableCD.isAncestorOrSameAs(intCD));
		
	}
	
	
}
