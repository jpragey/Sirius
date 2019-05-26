package org.sirius.frontend.core;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ModuleDeclaration;
import org.sirius.frontend.ast.PackageDeclaration;

public class ModuleContent {

	private Reporter reporter;
	private List<PackageContent> packageContents;
	private ModuleDeclaration moduleDeclaration;
	
	// cache, from module declaration
	private PhysicalPath modulePath; 
			
	public ModuleContent(Reporter reporter, ModuleDeclaration moduleDeclaration)
	{
		super();
		this.reporter = reporter;
		this.moduleDeclaration = moduleDeclaration;
		
		this.modulePath = new PhysicalPath(moduleDeclaration.getqName().getStringElements());
		
		this.packageContents = moduleDeclaration
				.getPackageDeclarations()
				.stream()
				.map(pd -> new PackageContent(pd))
				.collect(Collectors.toList());
	}
	
	public List<PackageContent> getPackageContents() {
		return packageContents;
	}
	
	public void addPackageContents(List<PackageContent> packageContents) {
		for(PackageContent pc: packageContents)
			addPackage(pc);
	}
	
	public void addPackage(PackageContent packageContent) {
		this.moduleDeclaration.addPackageDeclaration(packageContent.getPackageDeclaration());
		this.packageContents.add(packageContent);
	}
	
	/** Create an empty package with same qname as this module, if there's no matching package declarator.
	 * 
	 */
	public void createDefaultPackageIfNeeded() {
		if(packageContents.isEmpty()) {
			PackageContent pc = new PackageContent(new PackageDeclaration(reporter));
			pc.setPackageQName(moduleDeclaration.getqName().getElements());
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
