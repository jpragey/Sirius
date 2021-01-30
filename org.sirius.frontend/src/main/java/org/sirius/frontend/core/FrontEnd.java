package org.sirius.frontend.core;

import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.stdlayout.ModuleFiles;
import org.sirius.frontend.core.stdlayout.PackageFiles;
import org.sirius.frontend.core.stdlayout.StdInputTextProvider;


/** Builder for frontend parsing.
 * 
 * @author jpragey
 *
 */
public class FrontEnd {
	private Reporter reporter;
	
	public FrontEnd(Reporter reporter) {
		super();
		this.reporter = reporter;
	}
	
	public ScriptSession createScriptSession(InputTextProvider provider) {
		ScriptSession session = new ScriptSession(reporter, provider);
		return session;
	}
}
