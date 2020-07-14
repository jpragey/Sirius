package org.sirius.frontend.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class AstClassDeclarationTest {

	private Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}
	
	@AfterEach
	public void teardown() {
		assertTrue(reporter.ok());
	}

	@Test
	public void inheritsTest() {
		
		DefaultSymbolTable symbolTable = new DefaultSymbolTable("AstClassDeclarationTest");
		
		AstClassDeclaration ancestor = new AstClassDeclaration(reporter, AstToken.internal("ancestor"));
		
		AstClassDeclaration child = new AstClassDeclaration(reporter, AstToken.internal("child"));
		
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
