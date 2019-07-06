package org.sirius.frontend.sdk;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.symbols.GlobalSymbolTable;
import org.sirius.frontend.symbols.Symbol;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SdkToolsTest {
	private Reporter reporter;
	private GlobalSymbolTable symbolTable ;
	private SdkTools sdkTools;
	
	@BeforeMethod
	public void setup() throws Exception {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		this.sdkTools = new SdkTools(reporter);
		symbolTable = new GlobalSymbolTable();
		sdkTools.parseSdk(symbolTable);
		
		if(this.reporter.hasErrors()) 
			throw new Exception("SDK error, see logs in shell");
		
	}
	
	@Test
	public void sdkParsingMustCreateBasicClasses() {
		assertEquals(reporter.getErrorCount(), 0);
		
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "Stringifiable"));
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "String"));
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "Addable"));
		checkSymbolTableContains(symbolTable, new QName("sirius", "lang", "Integer"));
	}
	
	private void checkSymbolTableContains(GlobalSymbolTable symbolTable, QName symbolQName) {
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
		
		assertEquals(cd.getAncestors().get(0).getPackageQName(), new QName("sirius", "lang"));
		assertEquals(cd.getAncestors().get(0).getClassName(), "Addable");
		
		assertEquals(cd.getAncestors().get(1).getPackageQName(), new QName("sirius", "lang"));
		assertEquals(cd.getAncestors().get(1).getClassName(), "Stringifiable");
	}
	
	@Test
	public void checkBasicTopLevelFunctionsFoundInPackageClass() {
		
		Symbol symbol = symbolTable.lookup(new QName("sirius", "lang", "println")).get();
		AstFunctionDeclaration func = symbol.getFunctionDeclaration().get();
		
		assertEquals(func.getQName(), new QName("sirius", "lang", "println"));
		assertEquals(func.getFormalArguments().size(), 1);
		
		AstFunctionFormalArgument arg0 = func.getFormalArguments().get(0); 
		assertEquals(arg0.getName().getText(), "text"); // TODO
		assert(arg0.getType() instanceof SimpleType);
		SimpleType arg0Type = (SimpleType)arg0.getType();
		assertEquals(arg0Type.getName().getText(), "sirius.lang.String");
		
		
	}
	
}
