package org.sirius.frontend.core.stdlayout;

import java.util.List;

import org.sirius.frontend.core.InputTextProvider;

public class ModuleFiles {
	private InputTextProvider moduleDescriptor;
	private List<PackageFiles> packages;
	
	
	public ModuleFiles(InputTextProvider moduleDescriptor, List<PackageFiles> packages/* StdInputTextProvider packageDescriptor, List<StdInputTextProvider> sourceFiles*/) {
		super();
		this.moduleDescriptor = moduleDescriptor;
		this.packages = packages;
	}
	public InputTextProvider getModuleDescriptor() {
		return moduleDescriptor;
	}
	
	public List<PackageFiles> getPackages() {
		return packages;
	}
}