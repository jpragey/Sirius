package org.sirius.compiler.core;

import java.util.List;

import org.sirius.backend.core.Backend;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;

public class ScriptCompiler implements Compiler {

	private Reporter reporter;
	private List<Backend> backends;
	private FrontEnd frontEnd;
	
	public ScriptCompiler(Reporter reporter, List<Backend> backends, FrontEnd frontEnd) {
		super();
		this.reporter = reporter;
		this.backends = backends;
		this.frontEnd = frontEnd;

	}

	@Override
	public void compile() {
//		InputTextProvider provider = 
//		frontEnd.createScriptSession(provider)

	}

}
