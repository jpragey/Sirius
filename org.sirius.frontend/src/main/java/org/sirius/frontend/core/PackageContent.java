package org.sirius.frontend.core;

import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.PackageDeclaration;

public class PackageContent {

	private PackageDeclaration packageDeclaration;
	private PhysicalPath physicalPath;

	public PackageContent(PackageDeclaration packageDeclaration) {
		super();
		this.packageDeclaration = packageDeclaration;
		this.physicalPath = new PhysicalPath(packageDeclaration.getPathElements());
	}

	public static PackageContent createEmpty(Reporter reporter) {
		return new PackageContent(new PackageDeclaration(reporter));
	}

	public PackageDeclaration getPackageDeclaration() {
		return packageDeclaration;
	}

	/** Expected physical path, from packageDeclaration *content*
	 * 
	 * @return
	 */
	public PhysicalPath getPhysicalPath() {
		return physicalPath;
	}
	
	public void setPackageQName(List<AstToken> parts) {
		for(AstToken part: parts) {
			this.packageDeclaration.addNamePart(part);
		}
	}

	@Override
	public String toString() {
		
		return "pkgc:" + physicalPath.toString() + ":" + packageDeclaration.getQnameString();
	}
}
