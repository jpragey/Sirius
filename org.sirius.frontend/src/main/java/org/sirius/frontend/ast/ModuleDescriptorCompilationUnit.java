package org.sirius.frontend.ast;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;

public class ModuleDescriptorCompilationUnit implements AbstractCompilationUnit, Visitable {

	private ModuleDeclaration moduleDeclaration;
	private Reporter reporter; 
	
	public ModuleDescriptorCompilationUnit(Reporter reporter, ModuleDeclaration moduleDeclaration) {
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
	
	
	public ModuleDeclaration getModuleDeclaration() {
		return moduleDeclaration;
	}

}
