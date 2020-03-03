package org.sirius.frontend.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

	@Override
	public void updateParentsDeeply() {
		// Nothing to do
	}

	@Override
	public List<AstModuleDeclaration> getModuleDeclarations() {
		return Arrays.asList(moduleDeclaration);
	}

	@Override
	public Optional<ShebangDeclaration> getShebangDeclaration() {
		return Optional.empty();
	}

	@Override
	public AstModuleDeclaration getCurrentModule() {
		return moduleDeclaration;
	}

}
