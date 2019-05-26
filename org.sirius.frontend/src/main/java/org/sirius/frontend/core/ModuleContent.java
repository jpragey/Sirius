package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.ModuleDeclaration;
import org.sirius.frontend.ast.PackageDeclaration;

public class ModuleContent {

	private Reporter reporter;
	private List<PackageDeclaration> packageContents = new ArrayList<>();
	private ModuleDeclaration moduleDeclaration;
	
	// cache, from module declaration
	private PhysicalPath modulePath; 
			
	public ModuleContent(Reporter reporter, ModuleDeclaration moduleDeclaration)
	{
		super();
		this.reporter = reporter;
		this.moduleDeclaration = moduleDeclaration;
		
		this.modulePath = new PhysicalPath(moduleDeclaration.getqName().getStringElements());
		
		this.packageContents.addAll(moduleDeclaration.getPackageDeclarations());
	}
	
	public List<PackageDeclaration> getPackageContents() {
		return packageContents;
	}
	
	public void addPackageContents(List<PackageDeclaration> packageContents) {
		for(PackageDeclaration pc: packageContents)
			addPackage(pc);
	}
	
	public void addPackage(PackageDeclaration packageContent) {
		this.moduleDeclaration.addPackageDeclaration(packageContent);
		this.packageContents.add(packageContent);
	}
	
	/** Create an empty package with same qname as this module, if there's no matching package declarator.
	 * 
	 */
	public void createDefaultPackageIfNeeded() {
		if(packageContents.isEmpty()) {
			
			PackageDeclaration pc = new PackageDeclaration(reporter, moduleDeclaration.getqName().getElements());
			packageContents.add(pc);
		}
	}

	public ModuleDeclaration getModuleDeclaration() {
		return moduleDeclaration;
	}

	public List<String> getQName() {
		return moduleDeclaration.getqName().getStringElements();	// TODO cache?
	}

	public PhysicalPath getModulePath() {
		return modulePath;
	}
	
	@Override
	public String toString() {
		return modulePath.getElements().toString();
	}

}
