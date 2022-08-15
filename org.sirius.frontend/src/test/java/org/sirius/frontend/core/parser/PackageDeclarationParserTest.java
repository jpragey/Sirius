package org.sirius.frontend.core.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.SiriusParser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


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
				
		Parsers.PackageDeclarationVisitor visitor = new Parsers(reporter).new PackageDeclarationVisitor();
		AstPackageDeclaration interfaceDeclaration = visitor.visit(tree);
		return interfaceDeclaration;
	}
	
	@Test
	@DisplayName("Simplest package declarations")
	public void simplestPackageDeclarations() {
		simplestPackageCheck("package a.b.c;", pkg -> {
			assertEquals(pkg.getQname().getStringElements(), List.of("a", "b", "c"));
		});
	}
	
	@Test
	@DisplayName("Package with top-level functions")
	public void packageWithTLFunctions() {
			simplestPackageCheck("package a.b.c; void f(){} void g(){} void h(){} ", pkg -> {
			assertEquals(pkg.getQname().getStringElements(), List.of("a", "b", "c"));
			assertThat(pkg.getFunctionDeclarations().size(), equalTo(3));
			assertThat(pkg.getFunctionDeclarations().get(0).getNameString(), equalTo("f"));
			assertThat(pkg.getFunctionDeclarations().get(1).getNameString(), equalTo("g"));
			assertThat(pkg.getFunctionDeclarations().get(2).getNameString(), equalTo("h"));
		});
	}
	
	public AstPackageDeclaration simplestPackageCheck(String inputText, Consumer<AstPackageDeclaration> verify) {
		AstPackageDeclaration myPackage = parsePackageDeclaration(inputText);
		verify.accept(myPackage);
		return myPackage;
	}
}
