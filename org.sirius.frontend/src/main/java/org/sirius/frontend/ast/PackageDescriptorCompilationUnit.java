package org.sirius.frontend.ast;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;

/** CompilationUnit from package descriptor */
public class PackageDescriptorCompilationUnit implements AbstractCompilationUnit, Visitable {

	private PackageDeclaration packageDeclaration;
	private Reporter reporter; 
	
	public PackageDescriptorCompilationUnit(Reporter reporter, PackageDeclaration packageDeclaration) {
		super();
		this.reporter = reporter;
		this.packageDeclaration = packageDeclaration;
	}
		
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startPackageDescriptorCompilationUnit(this);
		
		this.packageDeclaration.visit(visitor);
		
		visitor.endPackageDescriptorCompilationUnit(this);
	}
	
	
	public PackageDeclaration getPackageDeclaration() {
		return packageDeclaration;
	}

}
