package org.sirius.frontend.core;

import java.util.List;

import org.sirius.frontend.api.ModuleDeclaration;

public interface Session {

	List<ModuleContent> getModuleContents();
	
	/** Convert to frontend API ModuleDeclaration
	 * 
	 * @return
	 */
	List<ModuleDeclaration> getModuleDeclarations();

	
}
