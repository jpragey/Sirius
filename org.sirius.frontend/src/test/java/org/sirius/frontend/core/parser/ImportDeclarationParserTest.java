package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
//import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

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
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.parser.SiriusParser;

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
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.importDeclaration();
				
		ImportDeclarationParser.PackageDeclarationVisitor visitor = new ImportDeclarationParser.PackageDeclarationVisitor(reporter);
		ImportDeclaration importDeclaration = visitor.visit(tree);
		return importDeclaration;
	}
	
	@Test
	@DisplayName("Simple import declarations")
	public void simplestImportDeclarations() {
		importCheck("import a.b.c {}", impDecl -> {
			List<String> pack = impDecl.getPack().toQName().getStringElements();
			assertThat(pack, equalTo(List.of("a", "b", "c")));
			assertThat(impDecl.getElements().size(), equalTo(0));
		});
		importCheck("import a.b.c;", impDecl -> {
			List<String> pack = impDecl.getPack().toQName().getStringElements();
			assertThat(pack, equalTo(List.of("a", "b", "c")));
			assertThat(impDecl.getElements().size(), equalTo(0));
		});
	}
	
	@Test
	@DisplayName("Import specific elemnts")
	public void importElementsDeclarations() {
		importCheck("import pack {A,B}", impDecl -> {
			assertThat(impDecl.getElements().size(), equalTo(2));
			
			ImportDeclarationElement elem0 = impDecl.getElements().get(0);
			assertThat(elem0.getImportedTypeName().getText(), equalTo("A"));
			assertThat(elem0.getAlias().isPresent(), equalTo(false));

			ImportDeclarationElement elem1 = impDecl.getElements().get(1);
			assertThat(elem1.getImportedTypeName().getText(), equalTo("B"));
			assertThat(elem1.getAlias().isPresent(), equalTo(false));
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
