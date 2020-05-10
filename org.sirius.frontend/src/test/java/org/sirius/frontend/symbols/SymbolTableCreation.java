package org.sirius.frontend.symbols;

import static org.testng.Assert.assertEquals;

import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class SymbolTableCreation {

	@Test(enabled = false)
	void testCUSymbolTablesContainsImportedSymbols() {
//		
//		List<ModuleContent> mcs = Compiler.compile(
//				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"),
//				new TextInputTextProvider("a/b", "A.sirius", "import a.b {C}")
//				);
//
//		ModuleContent mc = mcs.get(0);
//		assertNotNull(mc);
//		
//		CompilationUnit cu = mc.getCompilationUnits().get(0);
//		
//		AliasingSymbolTable st = cu.getSymbolTable();
//		
//		Optional<ImportedSymbol> s = st.lookupImport("C");
//		assertTrue(s.isPresent());
	}

	@Test(enabled = true)
	void testImportedSymbols() {
		ScriptSession session = Compiler.compileScript(
				"#!\n"
				+ "import sirius.lang {String} "
				+ "module a.b \"1.0\" {} "
				+ "void f(){println(\"Hello World\");}");
		
		DefaultSymbolTable st = session.getGlobalSymbolTable();
//		st.dump();
		
		
		ModuleDeclaration md = session.getModuleDeclarations().get(1);
		
		assertEquals(md.getPackages().size(), 1);
		PackageDeclaration pd = md.getPackages().get(0);
		
		TopLevelFunction fd = pd.getFunctions().get(0);
//		
//		List<ModuleContent> mcs = Compiler.compile(
//				new TextInputTextProvider("proj/provider", "module.sirius", "module proj.provider \"1\" {}"),
//				new TextInputTextProvider("proj/provider", "Prov.sirius", "Integer ff() {} ")
////				new TextInputTextProvider("proj/consumer", "module.sirius", "module proj.consumer \"1\" {import proj.provider \"1\"}"),
////				new TextInputTextProvider("proj/consumer", "Cons.sirius", "import proj.provider {f}")
//				);
//
////		ModuleContent conMC = mcs.stream().filter(mc -> mc.getPkgName() == "proj/consumer").findFirst().get();
//		
////		ModuleContent mc = mcs.get(0);
////		assertNotNull(mc);
////		
////		CompilationUnit cu = mc.getCompilationUnits().get(0);
////		
////		AliasingSymbolTable st = cu.getSymbolTable();
////		
////		Optional<ImportedSymbol> s = st.lookupImport("C");
////		assertTrue(s.isPresent());
	}

}
