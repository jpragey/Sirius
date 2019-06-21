package org.sirius.frontend.ast;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;

/** CompilationUnit from module descriptor */
public class ModuleDescriptor implements AbstractCompilationUnit, Visitable {

	private AstModuleDeclaration moduleDeclaration;
	private Reporter reporter; 
	
	public ModuleDescriptor(Reporter reporter, AstModuleDeclaration moduleDeclaration) {
		super();
		this.reporter = reporter;
		this.moduleDeclaration = moduleDeclaration;
	}
		
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startModuleDescriptorCompilationUnit(this);
		
		this.moduleDeclaration.visit(visitor);
		visitor.endModuleDescriptorCompilationUnit(this);
	}
	
	
	public AstModuleDeclaration getModuleDeclaration() {
		return moduleDeclaration;
	}

}
