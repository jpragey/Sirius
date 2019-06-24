package org.sirius.compiler.core;

import java.util.Arrays;
import java.util.List;

import org.sirius.backend.core.Backend;
import org.sirius.common.error.Reporter;
import org.sirius.compiler.options.AbstractOptionValues;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;

public class StandardCompiler implements Compiler {
	private Reporter reporter;
	private List<Backend> backends;
	private FrontEnd frontEnd;
	private List<InputTextProvider> inputProviders;
	private AbstractOptionValues options;
	
	public StandardCompiler(Reporter reporter, List<Backend> backends, FrontEnd frontEnd, List<InputTextProvider> inputProviders, AbstractOptionValues options) {
		super();
		this.reporter = reporter;
		this.backends = backends;
		this.frontEnd = frontEnd;
		this.inputProviders = inputProviders;
		this.options = options;
	}


	@Override
	public void compile() {
		
		for(InputTextProvider provider: inputProviders) 
			compileSingleInput(provider);
	}

	public void compileSingleInput(InputTextProvider provider) {
		Session session = frontEnd.createStandardSession(Arrays.asList(provider));

		for(Backend backend: backends) {
			backend.process(session);
		}
	}

}
