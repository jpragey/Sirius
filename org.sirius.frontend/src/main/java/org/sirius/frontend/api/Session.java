package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.core.InputTextProvider;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public interface Session {
	
	/** Convert to frontend API ModuleDeclaration
	 * 
	 * @return
	 */
	List<ModuleDeclaration> getModuleDeclarations();

	Reporter getReporter();


	default void applyVisitors(Reporter reporter, AbstractCompilationUnit compilationUnit, AstVisitor... visitors) {
		for(AstVisitor v: visitors) {
			compilationUnit.visit(v);
			if(reporter.hasErrors()) {
				return;
			}
		}
	}
	

	default void stdTransform(Reporter reporter, InputTextProvider input, AbstractCompilationUnit compilationUnit, DefaultSymbolTable globalSymbolTable) {
		StdAstTransforms.insertPackagesInModules(reporter, compilationUnit);
		
		// -- Set qualified names 
		StdAstTransforms.setQNames(compilationUnit);
		
		// -- Set scopes
		StdAstTransforms.setScopes(compilationUnit);
		
		StdAstTransforms.linkClassesToInterfaces(reporter, compilationUnit);
			
		// -- Set symbol tables (thus create the ST tree), add symbols to tables
		StdAstTransforms.fillSymbolTables(compilationUnit, globalSymbolTable);

		// -- Resolve symbols in expressions
		StdAstTransforms.resolveSymbols(reporter, compilationUnit, globalSymbolTable);
	}
	
}
