package org.sirius.compiler.core;

import java.util.Arrays;
import java.util.List;

import org.sirius.backend.core.Backend;
import org.sirius.common.error.Reporter;
import org.sirius.compiler.options.AbstractOptionValues;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;
import org.sirius.frontend.core.StandardSession;
import org.sirius.frontend.core.stdlayout.ModuleFiles;

public class StandardCompiler implements Compiler {
	private Reporter reporter;
	private List<Backend> backends;
	private FrontEnd frontEnd;
	private List<ModuleFiles> moduleFiles;
	private AbstractOptionValues options;
	
	public StandardCompiler(Reporter reporter, List<Backend> backends, FrontEnd frontEnd, 
			List<ModuleFiles> moduleFiles,
			AbstractOptionValues options) {
		super();
		this.reporter = reporter;
		this.backends = backends;
		this.frontEnd = frontEnd;
		this.moduleFiles = moduleFiles;
		this.options = options;
	}

	@Override
	public void compile() {
		StandardSession session = new StandardSession(reporter, moduleFiles);

		for(Backend backend: backends) {
			backend.process(session);
		}
	}
}
