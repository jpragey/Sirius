package org.sirius.frontend.symbols;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.parser.Compiler;

public class SymbolTableCreation {


	@Test
	void testImportedSymbols() {
		ScriptSession session = Compiler.compileScript(
				"#!\n"
				+ "import sirius.lang {String} "
				+ "module a.b \"1.0\" {} "
				+ "void f(){println(\"Hello World\");}");
		
		SymbolTableImpl st = session.getCompilationUnit().getScope().getSymbolTable() /*.getGlobalSymbolTable()*/;
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		assertEquals(md.getPackages().size(), 1);
		PackageDeclaration pd = md.getPackages().get(0);
		
		AbstractFunction fd = pd.getFunctions().get(0);
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
