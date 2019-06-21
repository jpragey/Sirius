package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class ScriptCompilationUnit implements AbstractCompilationUnit, Visitable, Scoped {

	
	
	private Optional<ShebangDeclaration> shebangDeclaration = Optional.empty();

	private ArrayList<AstModuleDeclaration> moduleDeclarations = new ArrayList<>();
	private AstModuleDeclaration currentModule;
	
	private Reporter reporter; 
	
	private LocalSymbolTable symbolTable; 
	
	
	public ScriptCompilationUnit(Reporter reporter) {
		super();
		this.reporter = reporter;
		this.symbolTable = new LocalSymbolTable(reporter);
	
		this.addModuleDeclaration(new AstModuleDeclaration(reporter));
	}

	public void setShebang(ShebangDeclaration declaration) {
		this.shebangDeclaration = Optional.of(declaration);
	}
	
	public Optional<ShebangDeclaration> getShebangDeclaration() {
		return shebangDeclaration;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
//		visitor.startScriptCompilationUnit(this);
//		
//		this.visitables.stream().forEach(fd -> fd.visit(visitor));
////		if(shebangDeclaration.isPresent()) {
////			shebangDeclaration.get().visit(visitor);
////		}
////		
////		functionDeclarations.stream().forEach(fd -> fd.visit(visitor));
////		classDeclarations.stream().forEach(cd -> cd.visit(visitor));
//		visitor.endScriptCompilationUnit(this);
	}
	
	
	public AstModuleDeclaration getCurrentModule() {
		return currentModule;
	}

	public List<AstModuleDeclaration> getModuleDeclarations() {
		return moduleDeclarations;
	}

	public void addModuleDeclaration(AstModuleDeclaration moduleDeclaration) {
		this.currentModule = moduleDeclaration;
		this.moduleDeclarations.add(moduleDeclaration);
	}

	public void setSymbolTableParent(SymbolTable newParent) {
		this.symbolTable.setParentSymbolTable(newParent);
	}
	
	public void addPackageDeclaration(AstPackageDeclaration declaration) {
		this.currentModule.addPackageDeclaration(declaration);
	}

	public void addFunctionDeclaration(AstFunctionDeclaration d) {
		this.currentModule.addFunctionDeclaration(d);
	}
	public void addClassDeclaration(AstClassDeclaration d) {
		this.currentModule.addClassDeclaration(d);
	}
	
	
	@Override
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

}
