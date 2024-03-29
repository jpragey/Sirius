package org.sirius.frontend.api;

import java.util.Stack;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.symbols.QNameSetterVisitor;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.ScopeSetterVisitor;
import org.sirius.frontend.symbols.SymbolTableFillingVisitor;

public class StdAstTransforms {
	
	private static void applyVisitors(Reporter reporter, ScriptCompilationUnit compilationUnit, AstVisitor... visitors) {
		for(AstVisitor v: visitors) {
			compilationUnit.visit(v);
			if(reporter.hasErrors()) {
				return;
			}
		}
	}
	public static void insertPackagesInModules(Reporter reporter, ScriptCompilationUnit compilationUnit) {

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
			}

		};
		applyVisitors(reporter, compilationUnit, insertPackageInModulesVisitor);

	}

	public static void setQNames(ScriptCompilationUnit compilationUnit) {
		QNameSetterVisitor qNameSetterVisitor = new QNameSetterVisitor();
		compilationUnit.visit(qNameSetterVisitor);
	}

	public static void setScopes(ScriptCompilationUnit compilationUnit, Scope globalScope) {
		ScopeSetterVisitor visitor = new ScopeSetterVisitor(globalScope);
		compilationUnit.visit(visitor);
	}
	
	public static void fillSymbolTables(ScriptCompilationUnit compilationUnit, Scope scope) {
		SymbolTableFillingVisitor fillingVisitor = new SymbolTableFillingVisitor(scope);
		compilationUnit.visit(fillingVisitor);
	}

}