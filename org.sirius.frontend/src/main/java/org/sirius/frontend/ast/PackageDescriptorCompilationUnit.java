package org.sirius.frontend.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;

/** CompilationUnit from package descriptor */
public class PackageDescriptorCompilationUnit implements AbstractCompilationUnit, Visitable {

	private AstPackageDeclaration packageDeclaration;
	private Reporter reporter; 
	
	public PackageDescriptorCompilationUnit(Reporter reporter, AstPackageDeclaration packageDeclaration) {
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
	
	
	public AstPackageDeclaration getPackageDeclaration() {
		return packageDeclaration;
	}

	@Override
	public void updateParentsDeeply() {
		// Nothing to do
	}

	@Override
	public List<AstModuleDeclaration> getModuleDeclarations() {
		throw new UnsupportedOperationException("PackageDescriptorCompilationUnit.getModuleDeclarations() should be removed."); // TODO
	}

//	@Override
//	public AstModuleDeclaration getCurrentModule() {
//		throw new UnsupportedOperationException("PackageDescriptorCompilationUnit.getCurrentModule() should be removed."); // TODO
//	}

}
