package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.AliasingSymbolTable;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.GlobalSymbolTable;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class ScriptCompilationUnit implements AbstractCompilationUnit, Visitable, Scoped {

	
	
	private Optional<ShebangDeclaration> shebangDeclaration = Optional.empty();

	private List<ImportDeclaration> importDeclarations = new ArrayList<>();

	private LinkedList<AstModuleDeclaration> moduleDeclarations = new LinkedList<>();
	
	private Reporter reporter; 
	
	private DefaultSymbolTable symbolTable; 

	
	public ScriptCompilationUnit(Reporter reporter, DefaultSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		this.symbolTable = globalSymbolTable;
	
	}

	public void setShebang(ShebangDeclaration declaration) {
		this.shebangDeclaration = Optional.of(declaration);
	}
	
	public Optional<ShebangDeclaration> getShebangDeclaration() {
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
//		if(shebangDeclaration.isPresent()) {
//			shebangDeclaration.get().visit(visitor);
//		}
//		
//		functionDeclarations.stream().forEach(fd -> fd.visit(visitor));
//		classDeclarations.stream().forEach(cd -> cd.visit(visitor));
		visitor.endScriptCompilationUnit(this);
	}
	
	@Override
	public AstModuleDeclaration getCurrentModule() {
		AstModuleDeclaration last = moduleDeclarations.peekLast();
		if(last == null) {
			last = new AstModuleDeclaration(reporter);
			moduleDeclarations.add(last);
		}
		return last;
	}

	public AstPackageDeclaration getCurrentPackage() {
		return getCurrentModule().getCurrentPackage();
	}

	@Override
	public List<AstModuleDeclaration> getModuleDeclarations() {
		return moduleDeclarations;
	}

	public void addModuleDeclaration(AstModuleDeclaration moduleDeclaration) {
		moduleDeclaration.updatePackagesContainer();
		this.moduleDeclarations.add(moduleDeclaration);
	}

//	public void setSymbolTableParent(SymbolTable newParent) {
//		this.symbolTable.setParentSymbolTable(newParent);
//	}
	
	public void addPackageDeclaration(AstPackageDeclaration declaration) {
		getCurrentModule().addPackageDeclaration(declaration);
	}

	public void addFunctionDeclaration(AstFunctionDeclaration d) {
		getCurrentModule().addFunctionDeclaration(d);
	}
	public void addClassDeclaration(AstClassDeclaration d) {
		getCurrentModule().addClassDeclaration(d);
	}
	
	
	@Override
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	// TODO: could (?) be a visitor
	@Override
	public void updateParentsDeeply() {
		
		this.moduleDeclarations.stream()
			.forEach(AstModuleDeclaration::updatePackagesContainer);
	}
}
