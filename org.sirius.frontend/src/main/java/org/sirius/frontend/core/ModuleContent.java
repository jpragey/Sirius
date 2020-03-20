package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;

public class ModuleContent {

	private Reporter reporter;
	private List<AstPackageDeclaration> packageContents = new ArrayList<>();
	private AstModuleDeclaration moduleDeclaration;
	
	// cache, from module declaration
	private PhysicalPath modulePath; 
			
	public ModuleContent(Reporter reporter, AstModuleDeclaration moduleDeclaration)
	{
		super();
		this.reporter = reporter;
		this.moduleDeclaration = moduleDeclaration;
		
		this.modulePath = new PhysicalPath(moduleDeclaration.getqName().getStringElements());
		
		this.packageContents.addAll(moduleDeclaration.getPackageDeclarations());
	}
	
	public List<AstPackageDeclaration> getPackageContents() {
		return packageContents;
	}
	
	public void addPackageContents(List<AstPackageDeclaration> packageContents) {
		for(AstPackageDeclaration pc: packageContents)
			addPackage(pc);
	}
	
	public void addPackage(AstPackageDeclaration packageContent) {
		this.moduleDeclaration.addPackageDeclaration(packageContent);
		this.packageContents.add(packageContent);
	}
	
	/** Create an empty package with same qname as this module, if there's no matching package declarator.
	 * 
	 */
	public void createDefaultPackageIfNeeded() {
		if(packageContents.isEmpty()) {
			
			AstPackageDeclaration pc = new AstPackageDeclaration(reporter, moduleDeclaration.getqName());
			packageContents.add(pc);
		}
	}

	public AstModuleDeclaration getModuleDeclaration() {
		return moduleDeclaration;
	}

	public QName getQName() {
		return moduleDeclaration.getqName();
	}

	public PhysicalPath getModulePath() {
		return modulePath;
	}
	
	@Override
	public String toString() {
		return modulePath.getElements().toString();
	}

}
