package org.sirius.frontend.core.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ModuleImport;
import org.sirius.frontend.parser.SiriusParser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class ModuleDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	// -- Test of Module import
	private ModuleImport parseModuleImport(String inputText) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.moduleImport();
				
		ModuleDeclarationParser.ModuleImportVisitor visitor = new ModuleDeclarationParser.ModuleImportVisitor();
		ModuleImport moduleImport = visitor.visit(tree);
		return moduleImport;
		
	}
	
	@Test
	@DisplayName("Simplest module import")
	public void simplestModuleImport() {
		moduleImportCheck("import a.b.c \" 1.0 \" ;", md-> {
			assertTrue(md.getOrigin().isEmpty());
		});
	}
	
	@Test
	@DisplayName("Module import sharing")
	public void sharedModuleImport() {
		moduleImportCheck("import a.b.c \" 1.0 \" ;", md-> {
			assertThat(md.isShared(), equalTo(false));
		});
		moduleImportCheck("shared import a.b.c \" 1.0 \" ;", md-> {
			assertThat(md.isShared(), equalTo(true));
		});
	}
	
	@Test
	@DisplayName("Module import origin")
	public void moduleImportWithOrigin() {
		moduleImportCheck("import \"someorigin\": a \" 1.0 \" ;", md-> {
			assertTrue(md.getOrigin().isPresent());
			assertTrue(md.getOriginString().isPresent());
			assertThat(md.getOriginString().get(), equalTo("someorigin"));
		});
	}
	
	@Test
	@DisplayName("Module import qname as a QName")
	public void moduleImportWithQName() {
		moduleImportCheck("import a.b.c \"1\" ;", md-> {
			assertTrue(md.getQname().isPresent());
			assertThat(md.getQname().get(), equalTo(new QName("a", "b", "c")));
		});
	}
	@Test
	@DisplayName("Module import qname as a String")
	public void moduleImportWithQNameAsString() {
		moduleImportCheck("import \"some-library\" \"1\" ;", md-> {
			assertTrue(md.getQnameString().isPresent());
			assertThat(md.getQnameString().get(), equalTo("some-library"));
		});
	}
	
	@Test
	@DisplayName("Module import version (string)")
	public void moduleImportVersionAsString() {
		moduleImportCheck("import a \" 1 \" ;", md-> {
			assertThat(md.getVersionString(), equalTo("1"));
		});
	}
	@Test
	@DisplayName("Module import version (id)")
	public void moduleImportVersion() {
		moduleImportCheck("import a someVersion ;", md-> {
			assertThat(md.getVersionString(), equalTo("someVersion"));
			assertThat(md.getVersion().getText(), equalTo("someVersion"));
		});
	}
	
	public ModuleImport moduleImportCheck(String inputText, Consumer<ModuleImport> verify) {
		ModuleImport myModule = parseModuleImport(inputText);
		verify.accept(myModule);
		return myModule;
	}
	
	
	// -- Test of Module declaration
	private AstModuleDeclaration parseModuleDeclaration(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.moduleDeclaration();
				
		ModuleDeclarationParser.ModuleDeclarationVisitor visitor = new ModuleDeclarationParser.ModuleDeclarationVisitor(reporter
//				, List.of() 
//				new ModuleDeclarationParser.PackageElements()
				);
		AstModuleDeclaration moduleDeclaration = visitor.visit(tree).build(List.of() /*No package*/);
		return moduleDeclaration;
	}
	
	@Test
	@DisplayName("Simplest module declaration")
	public void simplestModuleDeclarations() {
		simplestModuleCheck("module a.b.c \" 1.0 \" {}", md-> {
			assertThat(md.getqName().dotSeparated(), equalTo("a.b.c"));
			assertThat(md.getVersion().getText(), equalTo("\" 1.0 \""));
			assertThat(md.getVersionString(), equalTo("1.0"));
		});
	}
	
	@Test
	@DisplayName("Module declaration with imports")
	public void moduleDeclarationsImports() {
		simplestModuleCheck("module a.b.c \"1\" { import lib0 \"1\"; import lib1 \"1\";   }", md-> {
			List<ModuleImport> imports = md.getModuleImports();
			assertThat(imports.size(), equalTo(2));
			assertThat(imports.get(0).getQname().get().dotSeparated(), equalTo("lib0"));
			assertThat(imports.get(1).getQname().get().dotSeparated(), equalTo("lib1"));
		});
	}
	
	@Test
	@DisplayName("Module declaration with equivalents")
	public void moduleDeclarationsEquivalents() {
		simplestModuleCheck("module a.b.c \"1\" { "
				+ "lib0version = \"0\"; "
				+ "import lib0 \"0\"; "
				+ "lib1version = \" 1 \"; "
				+ "import lib1 \"1\";   }", md-> {
					assertThat(md.getEquivalents().getEquivalentsMap().size(), equalTo(2));
					assertThat(md.getEquivalents().getTrimmed("lib0version").get(), equalTo("0"));
					assertThat(md.getEquivalents().getTrimmed("lib1version").get(), equalTo("1"));
		});
	}
	
	public AstModuleDeclaration simplestModuleCheck(String inputText, Consumer<AstModuleDeclaration> verify) {
		AstModuleDeclaration myModule = parseModuleDeclaration(inputText);
		verify.accept(myModule);
		return myModule;
	}
}
