package org.sirius.frontend.api;

import java.util.HashMap;
import java.util.Stack;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.QNameSetterVisitor;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.ScopeSetterVisitor;
import org.sirius.frontend.symbols.SymbolTableFillingVisitor;

public class StdAstTransforms {
	
	private static void applyVisitors(Reporter reporter, AbstractCompilationUnit compilationUnit, AstVisitor... visitors) {
		for(AstVisitor v: visitors) {
			compilationUnit.visit(v);
			if(reporter.hasErrors()) {
				return;
			}
		}
	}

	public static void linkClassesToInterfaces(Reporter reporter, AbstractCompilationUnit compilationUnit) {
		HashMap<String, AstInterfaceDeclaration> interfacesByName = new HashMap<>();

		AstVisitor interfaceCollectingVisitor = new AstVisitor() {
			@Override public void startInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
				interfacesByName.put(interfaceDeclaration.getNameString(), interfaceDeclaration);
			}
		};
		applyVisitors(reporter, compilationUnit, interfaceCollectingVisitor);

		AstVisitor interfaceResolutionVisitor = new AstVisitor() {
			@Override public void startClassDeclaration (AstClassDeclaration classDeclaration) {
				classDeclaration.resolveAncestors(interfacesByName);
			}
			@Override
			public void startInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
				interfaceDeclaration.resolveAncestors(interfacesByName);
			}
		};

		applyVisitors(reporter, compilationUnit, interfaceResolutionVisitor);
	}
	
	public static void insertPackagesInModules(Reporter reporter, AbstractCompilationUnit compilationUnit) {

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

	public static void setQNames(AbstractCompilationUnit compilationUnit) {
		QNameSetterVisitor qNameSetterVisitor = new QNameSetterVisitor();
		compilationUnit.visit(qNameSetterVisitor);
	}

	public static void setScopes(AbstractCompilationUnit compilationUnit, Scope globalScope) {
		ScopeSetterVisitor visitor = new ScopeSetterVisitor(globalScope);
		compilationUnit.visit(visitor);
	}
	
	public static void fillSymbolTables(AbstractCompilationUnit compilationUnit, Scope scope) {
		SymbolTableFillingVisitor fillingVisitor = new SymbolTableFillingVisitor(scope);
		compilationUnit.visit(fillingVisitor);
	}

}