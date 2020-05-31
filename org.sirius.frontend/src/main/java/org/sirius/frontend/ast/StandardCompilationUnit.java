package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class StandardCompilationUnit implements AbstractCompilationUnit, Visitable, Scoped {

	private List<ImportDeclaration> importDeclarations = new ArrayList<>();
	
	private List<AstFunctionDeclaration> functionDeclarations = new ArrayList<>();
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	private List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
	
	private ArrayList<AstModuleDeclaration> moduleDeclarations = new ArrayList<>();

	private Reporter reporter; 
	
	private DefaultSymbolTable symbolTable; 
	
	
	public StandardCompilationUnit(Reporter reporter, DefaultSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		this.symbolTable = new DefaultSymbolTable(globalSymbolTable);
	}


	public List<ImportDeclaration> getImportDeclarations() {
		return importDeclarations;
	}

	public void addImport(ImportDeclaration importDeclaration) {
		this.importDeclarations.add(importDeclaration);
		
		for(ImportDeclarationElement e: importDeclaration.getElements()) {
			this.symbolTable.addImportSymbol(importDeclaration.getPack(), e);
		}
	}

	public void addFunctionDeclaration(AstFunctionDeclaration /*.Builder*/ declaration) {
//		this.functionDeclarations.add(declaration.build(symbolTable /*TODO: ???*/  ));
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
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}

	@Override
	public void updateParentsDeeply() {
		// Nothing to do
	}

}
