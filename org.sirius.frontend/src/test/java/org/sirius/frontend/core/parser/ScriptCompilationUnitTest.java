package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

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
import org.sirius.frontend.ast.PackageDescriptorCompilationUnit;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.parser.SiriusParser;


@Disabled("Transitional")
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
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.scriptCompilationUnit2();
				
		ScriptCompilatioUnitParser.ScriptCompilationUnitVisitor visitor = new ScriptCompilatioUnitParser.ScriptCompilationUnitVisitor(reporter);
		ScriptCompilationUnit packageCU = visitor.visit(tree);
		return packageCU;
		
	}
	
	@Test
//	@Disabled("Transitional")
	@DisplayName("Simplest script compilation unit")
	public void simplestPackageCU() {
		scriptCUCheck("#!\n", cu-> {
			assertTrue(cu.getShebangDeclaration().isEmpty());
		});
		scriptCUCheck("#! run it \n", cu-> {
			assertTrue(cu.getShebangDeclaration().isPresent());
			assertThat(cu.getShebangDeclaration().get().getTrimmedText(), equalTo("run it"));
		});
	}
	
	@Test
	@Disabled("Transitional")
	@DisplayName("Script compilation unit with imports")
	public void scriptCUWithImports() {
		scriptCUCheck("#!\nimport a.b.c; import a.b.d {}", cu-> {
			assertThat(cu.getImportDeclarations().size(), equalTo(2));
			assertThat(cu.getImportDeclarations().get(0).getPack().toQName().dotSeparated(), equalTo("a.b.c"));
			assertThat(cu.getImportDeclarations().get(1).getPack().toQName().dotSeparated(), equalTo("a.b.d"));
		});
	}
	
	@Test
	@Disabled("Transitional")
	@DisplayName("Script compilation unit with modules")
	public void scriptCUWithModules() {
		scriptCUCheck("#!\n module a.b.c \"42\" {} module a.b.d \"42\" {}", cu-> {
			assertThat(cu.getModuleDeclarations().size(), equalTo(2));
			assertThat(cu.getModuleDeclarations().get(0).getqName().dotSeparated(), equalTo("a.b.c"));
			assertThat(cu.getModuleDeclarations().get(1).getqName().dotSeparated(), equalTo("a.b.d"));
		});
	}
	
	@Test
	@Disabled("Transitional")
	@DisplayName("Script compilation unit with packages")
	public void scriptCUWithPackages() {
		scriptCUCheck("#!\n package a.b.c ; package a.b.d;", cu-> {
			assertThat(cu.getPackages().size(), equalTo(2));
			assertThat(cu.getPackages().get(0).getQname().dotSeparated(), equalTo("a.b.c"));
			assertThat(cu.getPackages().get(1).getQname().dotSeparated(), equalTo("a.b.d"));
		});
	}
	
	public ScriptCompilationUnit scriptCUCheck(String inputText, Consumer<ScriptCompilationUnit> verify) {
		ScriptCompilationUnit packageCU = parseScriptCU(inputText);
		verify.accept(packageCU);
		return packageCU;
	}
}
