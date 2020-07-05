package org.sirius.frontend.core.parser;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.SiriusParser;

public class PackageDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	private AstPackageDeclaration parsePackageDeclaration(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.packageDeclaration();
				
		PackageDeclarationParser.PackageDeclarationVisitor visitor = new PackageDeclarationParser.PackageDeclarationVisitor(reporter);
		AstPackageDeclaration interfaceDeclaration = visitor.visit(tree);
		return interfaceDeclaration;
	}
	
	@Test
	@DisplayName("Simplest package declarations")
	public void simplestPackageDeclarations() {
		simplestPackageCheck("package a.b.c;");
	}
	
	public void simplestPackageCheck(String inputText) {
		AstPackageDeclaration myPackage = parsePackageDeclaration(inputText);
		
		assertEquals(myPackage.getQname().getStringElements(), List.of("a", "b", "c"));
	}
}
