package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.parser.Sirius;

public class ImportDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	private ImportDeclaration parseImportDeclaration(String inputText) {
		
		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);

		ParseTree tree = parser.importDeclaration();
				
		ImportDeclarationParser.ImportDeclarationVisitor visitor = new ImportDeclarationParser(parserFactory.tokenStream()).new ImportDeclarationVisitor(reporter);
		ImportDeclaration importDeclaration = visitor.visit(tree);
		return importDeclaration;
	}
	
	@Test
	@DisplayName("Simple import declarations")
	public void simplestImportDeclarations() {
		importCheck("import a.b.c {}", impDecl -> {
			List<String> pack = impDecl.getPack().toQName().getStringElements();
			assertThat(pack, contains("a", "b", "c"));
			assertThat(impDecl.getElements(), empty());
		});
		importCheck("import a.b.c;", impDecl -> {
			List<String> pack = impDecl.getPack().toQName().getStringElements();
			assertThat(pack, contains("a", "b", "c"));
			assertThat(impDecl.getElements(), empty());
		});
	}
	
	@Test
	@DisplayName("Import specific elemnts")
	public void importElementsDeclarations() {
		importCheck("import pack {A,B}", impDecl -> {

			assertThat(impDecl.getElements().stream()
					.map(ImportDeclarationElement::getImportedTypeName)
					.map(AstToken::getText)
					.toList(), 
					contains("A", "B"));

			assertThat(impDecl.getElements().stream()
					.map(ImportDeclarationElement::getAlias)
					.map(Optional::isPresent)
					.toList(), 
					contains(false, false));
		});
	}
	
	@Test
	@DisplayName("Aliased import declaration")
	public void aliasedImport() {
		importCheck("import pack {A=AA}", impDecl -> {
			ImportDeclarationElement elem0 = impDecl.getElements().get(0);
			assertThat(elem0.getImportedTypeName().getText(), equalTo("AA"));
			assertThat(elem0.getAlias().isPresent(), equalTo(true));
			assertThat(elem0.getAlias().get().getText(), equalTo("A"));
		});
		importCheck("import pack {a=aa}", impDecl -> {
			ImportDeclarationElement elem0 = impDecl.getElements().get(0);
			assertThat(elem0.getImportedTypeName().getText(), equalTo("aa"));
			assertThat(elem0.getAlias().isPresent(), equalTo(true));
			assertThat(elem0.getAlias().get().getText(), equalTo("a"));
		});
	}
	
	public ImportDeclaration importCheck(String inputText, Consumer<ImportDeclaration> verify) {
		ImportDeclaration myImport = parseImportDeclaration(inputText);
		
		verify.accept(myImport);
		return myImport;
	}

}
