package org.sirius.frontend.transform;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.StandardCompilationUnit;

/** Regroup all top-level classes in a '$root$' class
 * 
 * @author jpragey
 *
 */
public class CreateRootClassTransformer implements /*Transformer*/ AstVisitor {
//	private static final String rootClassName = "$root$";
	
	private Reporter reporter;
	
	private AstClassDeclaration rootClass;
	
	public CreateRootClassTransformer(Reporter reporter, AstClassDeclaration moduleRootClass) {
		super();
		this.reporter = reporter;
		this.rootClass = moduleRootClass;
	}

	@Override
	public void startCompilationUnit(StandardCompilationUnit compilationUnit) {
		
		for(AstFunctionDeclaration fd: compilationUnit.getFunctionDeclarations()) {
			rootClass.addFunctionDeclaration(fd);
		}
		
		compilationUnit.addClassDeclaration(rootClass);
		compilationUnit.clearFunctionDeclarations();
	}
	
}
