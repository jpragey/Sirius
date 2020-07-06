package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class ScriptCompilationUnit implements AbstractCompilationUnit, Visitable, Scoped {

	
	
	private Optional<ShebangDeclaration> shebangDeclaration = Optional.empty();

	private List<ImportDeclaration> importDeclarations = new ArrayList<>();

	private List<AstModuleDeclaration> moduleDeclarations = new LinkedList<>();
	
	private List<AstPackageDeclaration> packages;	// TODO: remove, they belong to some module
	
	private Reporter reporter; 
	
	private DefaultSymbolTable symbolTable; 

	
	public ScriptCompilationUnit(Reporter reporter, DefaultSymbolTable globalSymbolTable,
			Optional<ShebangDeclaration> shebangDeclaration,
			List<ImportDeclaration> importDeclarations,
			List<AstPackageDeclaration> packages,
			List<AstModuleDeclaration> modules) {
		super();
		this.reporter = reporter;
		this.symbolTable = globalSymbolTable;

		assert(shebangDeclaration != null);
		this.shebangDeclaration = shebangDeclaration;
		this.importDeclarations = importDeclarations;
		this.moduleDeclarations = modules;
		
		this.packages = packages;
	}
//	public ScriptCompilationUnit(Reporter reporter, DefaultSymbolTable globalSymbolTable, 
//			AstModuleDeclaration rootModule) {
//		super();
//		this.reporter = reporter;
//		this.symbolTable = globalSymbolTable;
//		this.moduleDeclarations.add(rootModule);
//	}

	
	public List<AstPackageDeclaration> getPackages() {
		return packages;
	}
	public void setShebang(ShebangDeclaration declaration) {
		this.shebangDeclaration = Optional.of(declaration);
	}
	
	public Optional<ShebangDeclaration> getShebangDeclaration() {
		assert(shebangDeclaration != null);
		return shebangDeclaration;
	}

	public List<ImportDeclaration> getImportDeclarations() {
		return importDeclarations;
	}

	public void addImport(ImportDeclaration importDeclaration) {
		this.importDeclarations.add(importDeclaration);
		
		for(ImportDeclarationElement e: importDeclaration.getElements()) {
			this.symbolTable.addImportSymbol(importDeclaration.getPack(), e /* e.getImportedTypeName(), e.getAlias()*/);
		}
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startScriptCompilationUnit(this);
		
		this.moduleDeclarations.stream().forEach(fd -> fd.visit(visitor));
		visitor.endScriptCompilationUnit(this);
	}
	
	@Override
	public List<AstModuleDeclaration> getModuleDeclarations() {
		return moduleDeclarations;
	}

	public void addModuleDeclaration(AstModuleDeclaration moduleDeclaration) {
		this.moduleDeclarations.add(moduleDeclaration);
	}

	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}
}
