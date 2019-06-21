package org.sirius.frontend.symbols;

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

	@Test(enabled = false)
	void testImportedSymbols() {
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
