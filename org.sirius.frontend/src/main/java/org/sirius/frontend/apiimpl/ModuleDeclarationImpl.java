package org.sirius.frontend.apiimpl;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.PhysicalPath;

public class ModuleDeclarationImpl implements ModuleDeclaration {
	private QName moduleQName;
	private String version;
	private PhysicalPath physicalPath;
	private List<PackageDeclaration> packageDeclarationList;
	

	public ModuleDeclarationImpl(QName moduleQName, String version, PhysicalPath physicalPath, List<PackageDeclaration> packageDeclarationList) {
		super();
		this.moduleQName = moduleQName;
		this.version = version;
		this.physicalPath = physicalPath;
		this.packageDeclarationList = packageDeclarationList;
	}

	@Override
	public List<PackageDeclaration> getPackages() {
		return packageDeclarationList;
	}

	@Override
	public QName getQName() {
		return moduleQName;
	}

	@Override
	public PhysicalPath getPhysicalPath() {
		return physicalPath;
	}
	@Override
	public String toString() {
		return "\"" + getQName().toString() + "\"";
	}

	@Override
	public String getVersion() {
		return version;
	}
}
