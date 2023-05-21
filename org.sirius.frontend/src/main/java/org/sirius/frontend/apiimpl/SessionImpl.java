package org.sirius.frontend.apiimpl;

import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;

/** Default implementation of {@link Session}. Mainly for tests.
 * 
 * @author jpragey
 *
 */

public class SessionImpl implements Session {
	private List<ModuleDeclaration> moduleDeclarations;
	private Reporter reporter;
	
	public SessionImpl(Reporter reporter, List<ModuleDeclaration> moduleDeclarations) {
		super();
		this.reporter = reporter;
		this.moduleDeclarations = moduleDeclarations;
	}

	@Override
	public List<ModuleDeclaration> getModuleDeclarations() {
		return moduleDeclarations;
	}

	@Override
	public Reporter getReporter() {
		return reporter;
	}

}
