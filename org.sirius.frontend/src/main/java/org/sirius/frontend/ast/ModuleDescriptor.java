package org.sirius.frontend.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AbstractCompilationUnit;
import org.sirius.frontend.symbols.Scope;

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
	public List<AstModuleDeclaration> getModuleDeclarations() {
		return Arrays.asList(moduleDeclaration);
	}

	@Override
	public Scope getScope() {
		throw new UnsupportedOperationException("Not implemented yet"); // TODO ???
	}

}
