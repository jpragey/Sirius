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
import org.sirius.frontend.parser.Sirius;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTableImpl;


public class ScriptCompilationUnitTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	private ScriptCompilationUnit parseScriptCU(String inputText) {
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);
		Sirius parser = parserFactory.create();
		ParseTree tree = parser.scriptCompilationUnit();
				
		ScriptCompilatioUnitParser.ScriptCompilationUnitVisitor visitor = new ScriptCompilatioUnitParser.ScriptCompilationUnitVisitor(
				reporter, new Scope("ScriptCU"), parserFactory.tokenStream());
		ScriptCompilationUnit packageCU = visitor.visit(tree);
		return packageCU;
		
	}
	
	@Test
	@DisplayName("Simplest script compilation unit")
	public void simplestScriptCU() {
		scriptCUCheck("", cu-> {
			assertTrue(cu.getShebangDeclaration().isEmpty());
		});
		scriptCUCheck("#!\n", cu-> {
			assertTrue(cu.getShebangDeclaration().isPresent());
			assertThat(cu.getShebangDeclaration().get().getTrimmedText(), equalTo(""));
		});
		scriptCUCheck("#! run it \n", cu-> {
			assertTrue(cu.getShebangDeclaration().isPresent());
			assertThat(cu.getShebangDeclaration().get().getTrimmedText(), equalTo("run it"));
		});
	}
	
	@Test
	@DisplayName("Script compilation unit with imports")
	public void scriptCUWithImports() {
		scriptCUCheck("#!\nimport a.b.c; import a.b.d {}", cu-> {
			assertThat(cu.getImportDeclarations().size(), equalTo(2));
			assertThat(cu.getImportDeclarations().get(0).getPack().toQName().dotSeparated(), equalTo("a.b.c"));
			assertThat(cu.getImportDeclarations().get(1).getPack().toQName().dotSeparated(), equalTo("a.b.d"));
		});
	}
	
	@Test
	@DisplayName("Script compilation unit with modules")
	public void scriptCUWithModules() {
		scriptCUCheck("#!\n module a.b.c \"42\" {} module a.b.d \"42\" {}", cu-> {
			assertThat(cu.getModuleDeclarations().size(), equalTo(2));
			assertThat(cu.getModuleDeclarations().get(0).getqName().dotSeparated(), equalTo("a.b.c"));
			assertThat(cu.getModuleDeclarations().get(1).getqName().dotSeparated(), equalTo("a.b.d"));
		});
	}
	
	@Test
	@DisplayName("Script compilation with a top-level func must have un (unnamed) module")
	public void scriptWithTLFuncHasAModule() {
		scriptCUCheck("#!\n void f(){}", cu-> {
			assertThat(cu.getModuleDeclarations().size(), equalTo(1));
			assertThat(cu.getModuleDeclarations().get(0).getqName().dotSeparated(), equalTo(""));
		});
	}
	
	public ScriptCompilationUnit scriptCUCheck(String inputText, Consumer<ScriptCompilationUnit> verify) {
		ScriptCompilationUnit packageCU = parseScriptCU(inputText);
		verify.accept(packageCU);
		return packageCU;
	}
	
	// -- concrete modules
	private AstModuleDeclaration parseConcreteModule(String inputText) {
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);
		Sirius parser = parserFactory.create();
		ParseTree tree = parser.concreteModule();
				
		ModuleDeclarationParser.ConcreteModuleVisitor visitor = new ModuleDeclarationParser(reporter, parserFactory.tokenStream()).new ConcreteModuleVisitor();
		AstModuleDeclaration md = visitor.visit(tree);
		return md;
		
	}
	@Test
	@DisplayName("Module declaration with a top-level func must have un (unnamed) module")
	public void concreteModuleWithTLFuncHasAModule() {
		concreteModuleCheck("void f(){}", cu-> {
			assertThat(cu.getPackageDeclarations().size(), equalTo(1));
			AstPackageDeclaration pd = cu.getPackageDeclarations().get(0);
			assertThat(pd.getFunctionDeclarations().size(), equalTo(1));
			assertThat(pd.getFunctionDeclarations().get(0).getNameString(), equalTo("f"));
		});
		concreteModuleCheck("package a.b.c;", cu-> {
			assertThat(cu.getPackageDeclarations().size(), equalTo(1));
			assertThat(cu.getPackageDeclarations().get(0).getPackageDeclaration().qName().dotSeparated(), equalTo("a.b.c"));
		});
		concreteModuleCheck("class C() {}", cu-> {
			assertThat(cu.getPackageDeclarations().size(), equalTo(1));
			assertThat(cu.getPackageDeclarations().get(0).getClassDeclarations().size(), equalTo(1));
			assertThat(cu.getPackageDeclarations().get(0).getClassDeclarations().get(0).getName().getText(), equalTo("C"));
		});
		concreteModuleCheck("interface I {}", cu-> {
			assertThat(cu.getPackageDeclarations().size(), equalTo(1));
			assertThat(cu.getPackageDeclarations().get(0).getInterfaceDeclarations().size(), equalTo(1));
			assertThat(cu.getPackageDeclarations().get(0).getInterfaceDeclarations().get(0).getName().getText(), equalTo("I"));
		});
	}
	
	public AstModuleDeclaration concreteModuleCheck(String inputText, Consumer<AstModuleDeclaration> verify) {
		AstModuleDeclaration md = parseConcreteModule(inputText);
		verify.accept(md);
		return md;
	}
	
}
