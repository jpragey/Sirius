package org.sirius.frontend.api;

import java.util.List;

import org.sirius.frontend.core.ModuleContent;

public interface Session {

	List<ModuleContent> getModuleContents();
	
	/** Convert to frontend API ModuleDeclaration
	 * 
	 * @return
	 */
	List<ModuleDeclaration> getModuleDeclarations();

	
}
