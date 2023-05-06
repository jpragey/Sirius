package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.PackageDescriptorCompilationUnit;
import org.sirius.frontend.parser.Sirius;


public class PackageDescriptorCompilationUnitTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	private PackageDescriptorCompilationUnit parseModuleImport(String inputText) {
//		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);
		Sirius parser = parserFactory.create();

		ParseTree tree = parser.packageDescriptorCompilationUnit();
				
		PackageDescriptorCompilatioUnitParser.PackageDescriptorCompilationUnitVisitor visitor = 
				new PackageDescriptorCompilatioUnitParser.PackageDescriptorCompilationUnitVisitor(reporter, parserFactory.tokenStream());
		
		PackageDescriptorCompilationUnit packageCU = visitor.visit(tree);
		return packageCU;
		
	}
	
	@Test
	@DisplayName("Simplest package compilation unit")
	public void simplestPackageCU() {
		moduleImportCheck("package a.b.c;", cu-> {
			assertThat(cu.getPackageDeclaration().getQname().get().dotSeparated(), equalTo("a.b.c"));
		});
	}
	
	public PackageDescriptorCompilationUnit moduleImportCheck(String inputText, Consumer<PackageDescriptorCompilationUnit> verify) {
		PackageDescriptorCompilationUnit packageCU = parseModuleImport(inputText);
		verify.accept(packageCU);
		return packageCU;
	}
}
