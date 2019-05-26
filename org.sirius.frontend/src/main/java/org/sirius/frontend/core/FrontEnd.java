package org.sirius.frontend.core;

import java.util.List;

import org.sirius.common.error.Reporter;


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
	
	public StandardSession createStandardSession(List<InputTextProvider> inputTextProviders) {
		StandardSession session = new StandardSession(reporter, inputTextProviders);
		return session;
	}
	
	public ScriptSession createScriptSession(InputTextProvider provider) {
		ScriptSession session = new ScriptSession(reporter, provider);
		return session;
	}
	
	
}
