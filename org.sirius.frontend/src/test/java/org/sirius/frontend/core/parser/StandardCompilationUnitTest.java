package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.PackageDescriptorCompilationUnit;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.symbols.Scope;


public class StandardCompilationUnitTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	private StandardCompilationUnit parseScriptCU(String inputText) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.standardCompilationUnit();
				
		StandardCompilatioUnitParser.StandardCompilationUnitVisitor visitor = new StandardCompilatioUnitParser.StandardCompilationUnitVisitor(reporter, new Scope("Test global"));
		StandardCompilationUnit standardCU = visitor.visit(tree);
		return standardCU;
	}
	
	@Test
	@DisplayName("Simplest standard compilation unit - not implemented yet")
	public void simplestScriptCU() {
		standardCUCheck("", cu-> {
		});
	}
	public StandardCompilationUnit standardCUCheck(String inputText, Consumer<StandardCompilationUnit> verify) {
		StandardCompilationUnit packageCU = parseScriptCU(inputText);
		verify.accept(packageCU);
		return packageCU;
	}
}
