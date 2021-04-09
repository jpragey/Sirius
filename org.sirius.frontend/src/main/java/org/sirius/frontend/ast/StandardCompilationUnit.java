package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class StandardCompilationUnit implements AbstractCompilationUnit, Visitable, Scoped {

	private List<ImportDeclaration> importDeclarations = new ArrayList<>();
	
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	private List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
	
	private ArrayList<AstModuleDeclaration> moduleDeclarations = new ArrayList<>();

	private Reporter reporter; 
	
	private Scope globalScope;
	
	public StandardCompilationUnit(Reporter reporter, Scope globalScope) {
		super();
		this.reporter = reporter;
		this.globalScope = globalScope;
	}


	public List<ImportDeclaration> getImportDeclarations() {
		return importDeclarations;
	}

	public void addImport(ImportDeclaration importDeclaration) {
		this.importDeclarations.add(importDeclaration);
		
		for(ImportDeclarationElement e: importDeclaration.getElements()) {
			this.globalScope.getSymbolTable().addImportSymbol(importDeclaration.getPack(), e);
		}
	}

	public void clearFunctionDeclarations() {
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startCompilationUnit(this);
		importDeclarations.stream().forEach(imp -> imp.visit(visitor));
		classDeclarations.stream().forEach(cd -> cd.visit(visitor));
		visitor.endCompilationUnit(this);
	}
	
	public void addClassDeclaration(AstClassDeclaration classDeclaration) {
		this.classDeclarations.add(classDeclaration);
	}

	public void addInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		this.interfaceDeclarations.add(interfaceDeclaration);
	}

	public List<AstClassDeclaration> getClassDeclarations() {
		return classDeclarations;
	}
	
	public void addModuleDeclaration(AstModuleDeclaration declaration) {
		this.moduleDeclarations.add(declaration);
	}
	
	public List<AstModuleDeclaration> getModuleDeclarations() {
		return moduleDeclarations;
	}

	@Override
	public Scope getScope() {
		return globalScope;
	}

	@Override
	public void setScope2(Scope scope) {
		assert(this.globalScope == null);
		assert(scope != null);
		this.globalScope = scope;
	}
	
}
