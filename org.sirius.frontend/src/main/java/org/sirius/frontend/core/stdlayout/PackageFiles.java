package org.sirius.frontend.core.stdlayout;

import java.util.ArrayList;
import java.util.List;

import org.sirius.frontend.core.InputTextProvider;

public class PackageFiles {
	private InputTextProvider packageDescriptor;
	private List<InputTextProvider> sourceFiles;
	public PackageFiles(InputTextProvider packageDescriptor, List<? extends InputTextProvider> sourceFiles) {
		super();
		this.packageDescriptor = packageDescriptor;
		this.sourceFiles = new ArrayList<>(sourceFiles);
	}
	public InputTextProvider getPackageDescriptor() {
		return packageDescriptor;
	}
	public List<InputTextProvider> getSourceFiles() {
		return sourceFiles;
	}
}