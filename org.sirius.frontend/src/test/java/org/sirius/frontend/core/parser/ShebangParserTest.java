package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SiriusParser;

public class ShebangParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private ShebangDeclaration parseShebang(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.shebangDeclaration();
				
		ShebangDeclarationParser.ShebangVisitor visitor = new ShebangDeclarationParser.ShebangVisitor();
		ShebangDeclaration shebang = visitor.visit(tree);
		return shebang;
	}
	
	@Test
	@DisplayName("Simplest shebang declaration")
	public void simplestShebang() {
		ShebangDeclaration shebang = parseShebang("#!runTool");
		assertThat(shebang.getContentToken().getText(), equalTo("#!runTool"));
		assertThat(shebang.getTrimmedText(), equalTo("runTool"));
	}
	@Test
	@DisplayName("Simplest shebang declaration")
	public void shebangWithSpaces() {
		ShebangDeclaration shebang = parseShebang("#! run Tool ");
		assertThat(shebang.getContentToken().getText(), equalTo("#! run Tool "));
		assertThat(shebang.getTrimmedText(), equalTo("run Tool"));
	}
}
