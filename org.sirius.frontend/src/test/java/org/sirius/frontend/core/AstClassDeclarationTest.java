package org.sirius.frontend.core;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AstClassDeclarationTest {

	private Reporter reporter;

	@BeforeMethod
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}
	
	@AfterMethod
	public void teardown() {
		assertTrue(reporter.ok());
	}

	@Test
	public void inheritsTest() {
		
		DefaultSymbolTable symbolTable = new DefaultSymbolTable("AstClassDeclarationTest");
		
		AstClassDeclaration ancestor = AstClassDeclaration.newClass(reporter, AstToken.internal("ancestor"));
		
		AstClassDeclaration child = AstClassDeclaration.newClass(reporter, AstToken.internal("child"));
		
		QName pkgQName = new QName("some", "pkg");
		ancestor.setPackageQName(pkgQName);
		child.setPackageQName(pkgQName);
		
		
		child.addAncestor(ancestor.getName());
		
		symbolTable.addClass(ancestor);
		symbolTable.addClass(child);
		
		ancestor.setSymbolTable(symbolTable);
		child.setSymbolTable(symbolTable);
		
		assertTrue(ancestor.isAncestorOrSameAs(child));
		assertTrue(ancestor.isAncestorOrSameAs(ancestor));
		assertTrue(child.isAncestorOrSameAs(child));
		assertFalse(child.isAncestorOrSameAs(ancestor));
		
	}
}
