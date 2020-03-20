package org.sirius.frontend.core;

import java.util.List;

import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.Visitable;

public interface AbstractCompilationUnit extends Visitable {
	
	void updateParentsDeeply();
	
	List<AstModuleDeclaration> getModuleDeclarations();

}
