package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;

public class AstModuleContent {

	private List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
	private List<PartialList> functionsDeclarations = new ArrayList<>();
	private List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	
	
	public void addPackageDeclaration(AstPackageDeclaration packageDeclaration) {
		this.packageDeclarations.add(packageDeclaration);
	}
	
	public void addPartialList(PartialList partialList) {
		this.functionsDeclarations.add(partialList);
	}
	
	public void addInterface(AstInterfaceDeclaration interfaceDeclaration) {
		this.interfaceDeclarations.add(interfaceDeclaration);
	}
	
	public void addClass(AstClassDeclaration classDeclaration) {
		this.classDeclarations.add(classDeclaration);
	}
	
	
	
	public List<AstPackageDeclaration> getPackageDeclarations() {
		return packageDeclarations;
	}
	public List<PartialList> getFunctionsDeclarations() {
		return functionsDeclarations;
	}
	public List<AstInterfaceDeclaration> getInterfaceDeclarations() {
		return interfaceDeclarations;
	}
	public List<AstClassDeclaration> getClassDeclarations() {
		return classDeclarations;
	}
	
	
	
	
	
	
	
	
	
}
