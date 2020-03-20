package org.sirius.frontend.api;

import java.util.List;
import java.util.Stack;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.core.InputTextProvider;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.QNameSetterVisitor;
import org.sirius.frontend.symbols.SymbolResolutionVisitor;
import org.sirius.frontend.symbols.SymbolTableFillingVisitor;

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

		
		AstVisitor insertPackageInModulesVisitor = new AstVisitor() {
			Stack<AstModuleDeclaration> moduleStack = new Stack<AstModuleDeclaration>();

			@Override
			public void startModuleDeclaration(AstModuleDeclaration declaration) {
				moduleStack.push(declaration);
			}

			@Override
			public void endModuleDeclaration(AstModuleDeclaration declaration) {
				moduleStack.pop();
			}

			@Override
			public void startPackageDeclaration(AstPackageDeclaration declaration) {
				AstModuleDeclaration mod = moduleStack.peek();
				assert(mod != null);
//				mod.addPackageDeclaration(declaration);
			}
			
		};
		applyVisitors(reporter, compilationUnit, insertPackageInModulesVisitor);
		
		
		
		// -- Set qualified names 
		applyVisitors(reporter, compilationUnit, new QNameSetterVisitor());
		
		// -- Set symbol tables (thus create the ST tree), add symbols to tables
		applyVisitors(reporter, compilationUnit, new SymbolTableFillingVisitor(globalSymbolTable));

		// -- Resolve symbols in expressions
		applyVisitors(reporter, compilationUnit, new SymbolResolutionVisitor(reporter, globalSymbolTable));
		
	}
	
}
