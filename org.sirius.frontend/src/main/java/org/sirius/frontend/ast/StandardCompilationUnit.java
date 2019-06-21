package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.AliasingSymbolTable;
import org.sirius.frontend.symbols.GlobalSymbolTable;

public class StandardCompilationUnit implements AbstractCompilationUnit, Visitable, Scoped {

	private Optional<ShebangDeclaration> shebangDeclaration = Optional.empty();

	private List<ImportDeclaration> importDeclarations = new ArrayList<>();
	
	private List<AstFunctionDeclaration> functionDeclarations = new ArrayList<>();
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	
	private List<AstModuleDeclaration> moduleDeclarations = new ArrayList<>();

	private Reporter reporter; 
	
	private AliasingSymbolTable symbolTable; 
	
	
	public StandardCompilationUnit(Reporter reporter, GlobalSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		this.symbolTable = new AliasingSymbolTable(reporter, globalSymbolTable);
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
			this.symbolTable.addImportSymbol(importDeclaration.getPack(), e.getImportedTypeName(), e.getAlias());
		}
	}

	public void addFunctionDeclaration(AstFunctionDeclaration declaration) {
		this.functionDeclarations.add(declaration);
	}

	public void clearFunctionDeclarations() {
		functionDeclarations.clear();
	}
	
	public List<AstFunctionDeclaration> getFunctionDeclarations() {
		return functionDeclarations;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startCompilationUnit(this);
		importDeclarations.stream().forEach(imp -> imp.visit(visitor));
		functionDeclarations.stream().forEach(fd -> fd.visit(visitor));
		classDeclarations.stream().forEach(cd -> cd.visit(visitor));
		visitor.endCompilationUnit(this);
	}
	
	public void addClassDeclaration(AstClassDeclaration classDeclaration) {
		this.classDeclarations.add(classDeclaration);
	}

	public List<AstClassDeclaration> getClassDeclarations() {
		return classDeclarations;
	}
	
	public void addModuleDeclaration(AstModuleDeclaration declaration) {
		this.moduleDeclarations.add(declaration);
	}
	
//	public void setSymbolTableParent(SymbolTable newParent) {
//		this.symbolTable.setParentSymbolTable(newParent);
//	}
	
	public List<AstModuleDeclaration> getModuleDeclarations() {
		return moduleDeclarations;
	}

	@Override
	public AliasingSymbolTable getSymbolTable() {
		return symbolTable;
	}

}
