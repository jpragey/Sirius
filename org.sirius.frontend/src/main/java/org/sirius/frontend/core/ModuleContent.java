package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;

@Deprecated
public class ModuleContent {
/**
	private Reporter reporter;
	private List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
	private AstModuleDeclaration moduleDeclaration;
	
	// cache, from module declaration
	private PhysicalPath modulePath; 
			
	public ModuleContent(Reporter reporter, AstModuleDeclaration moduleDeclaration)
	{
		super();
		this.reporter = reporter;
		this.moduleDeclaration = moduleDeclaration;
		
		this.modulePath = new PhysicalPath(moduleDeclaration.getqName().getStringElements());
		
		this.packageDeclarations.addAll(moduleDeclaration.getPackageDeclarations());
	}
	
	public List<AstPackageDeclaration> getPackageContents() {
		return packageDeclarations;
	}
	public void addPackageDeclaration(AstPackageDeclaration pd) {
		packageDeclarations.add(pd);
	}
	public void addPackageDeclarations(List<AstPackageDeclaration> pd) {
		packageDeclarations.addAll(pd);
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
*/
}
