package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class ScriptCompilationUnit implements AbstractCompilationUnit, Visitable, Scoped {

	/** List of visitable children, in the order they are appended to this object. */
	private List<Visitable> visitables = new ArrayList<>();
	
	
	private Optional<ShebangDeclaration> shebangDeclaration = Optional.empty();

	private List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
	private List<ClassDeclaration> classDeclarations = new ArrayList<>();
	
	private Optional<ModuleDeclaration> moduleDeclaration = Optional.empty();
	
	private List<PackageDeclaration> packagesDeclarations = new ArrayList<>();
	private PackageDeclaration currentPackage;
	
	private Reporter reporter; 
	
	private LocalSymbolTable symbolTable; 
	
	
	public ScriptCompilationUnit(Reporter reporter) {
		super();
		this.reporter = reporter;
		this.symbolTable = new LocalSymbolTable(reporter);
		this.currentPackage = new PackageDeclaration(reporter);
	}

	public void setShebang(ShebangDeclaration declaration) {
		this.shebangDeclaration = Optional.of(declaration);
		this.visitables.add(declaration);
	}
	
	public Optional<ShebangDeclaration> getShebangDeclaration() {
		return shebangDeclaration;
	}
	
	public void addFunctionDeclaration(FunctionDeclaration declaration) {
		this.functionDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

//	public void clearFunctionDeclarations() {
//		functionDeclarations.clear();
//	}
	
	public List<FunctionDeclaration> getFunctionDeclarations() {
		return functionDeclarations;
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
	
	
	public Optional<ModuleDeclaration> getModuleDeclaration() {
		return moduleDeclaration;
	}

	public void setModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		this.moduleDeclaration = Optional.of(moduleDeclaration);
		this.visitables.add(moduleDeclaration);
	}

	public void addClassDeclaration(ClassDeclaration classDeclaration) {
		this.classDeclarations.add(classDeclaration);
		this.visitables.add(classDeclaration);
	}

	public List<ClassDeclaration> getClassDeclarations() {
		return classDeclarations;
	}
	
	public void setSymbolTableParent(SymbolTable newParent) {
		this.symbolTable.setParentSymbolTable(newParent);
	}
	
	public void addPackageDeclaration(PackageDeclaration declaration) {
		this.packagesDeclarations.add(declaration);
		this.currentPackage = declaration;
		this.visitables.add(declaration);
	}
	
	@Override
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

}
