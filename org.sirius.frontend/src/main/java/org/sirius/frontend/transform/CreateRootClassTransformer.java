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
//	private PackageDeclaration rootPackageDeclaration;
	
	public CreateRootClassTransformer(Reporter reporter/*, PackageDeclaration rootPackageDeclaration*/, AstClassDeclaration moduleRootClass) {
		super();
		this.reporter = reporter;
//		this.rootPackageDeclaration = rootPackageDeclaration;
		this.rootClass = moduleRootClass;
	}


//	@Override
//	public CompilationUnit transform(CompilationUnit compilationUnit) {
//		
//		AstToken name = new AstToken(0, 0, 0, 0, rootClassName);
//		
//		ClassDeclaration rootClass = new ClassDeclaration(reporter, false /*is interface*/, name, PackageDeclaration.root/*TODO: ???*/);
//		
//		for(FunctionDeclaration fd: compilationUnit.getFunctionDeclarations()) {
//			rootClass.addFunctionDeclaration(fd);
//		}
//		
//		compilationUnit.addClassDeclaration(rootClass);
//		compilationUnit.clearFunctionDeclarations();
//		
//		return compilationUnit;
//	}
	
	@Override
	public void startCompilationUnit(StandardCompilationUnit compilationUnit) {
//		AstToken name = new AstToken(0, 0, 0, 0, rootClassName, "<unknown>");
//		
//		ClassDeclaration rootClass = new ClassDeclaration(reporter, false /*is interface*/, name /*, rootPackageDeclaration /*PackageDeclaration.root*/ /*TODO: ???*/);
		
		for(AstFunctionDeclaration fd: compilationUnit.getFunctionDeclarations()) {
			rootClass.addFunctionDeclaration(fd);
		}
		
		compilationUnit.addClassDeclaration(rootClass);
		compilationUnit.clearFunctionDeclarations();
	}
	
}
