package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.core.InputTextProvider;

public interface Session {
	
	/** Convert to frontend API ModuleDeclaration
	 * 
	 * @return
	 */
	List<ModuleDeclaration> getModuleDeclarations();

	Reporter getReporter();
	
}
