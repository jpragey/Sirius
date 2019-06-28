package org.sirius.compiler.core;

import java.util.List;
import java.util.function.Function;

import org.sirius.backend.core.Backend;
import org.sirius.common.error.Reporter;
import org.sirius.compiler.options.CompileOptionsValues;
import org.sirius.compiler.options.RootOptionValues;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;

public class ScriptCompiler implements Compiler {

	private Reporter reporter;
	private List<Backend> backends;
	private FrontEnd frontEnd;
	private RootOptionValues optionValues;
	private CompileOptionsValues compileValues;
	private Function<String, InputTextProvider> inputTextFactory;
	
	
	public ScriptCompiler(Reporter reporter, List<Backend> backends, FrontEnd frontEnd,
			RootOptionValues optionValues,
			CompileOptionsValues compileValues,
			Function<String, InputTextProvider> inputTextFactory

			) {
		super();
		this.reporter = reporter;
		this.backends = backends;
		this.frontEnd = frontEnd;
		this.optionValues = optionValues;
		this.compileValues = compileValues;
		this.inputTextFactory = inputTextFactory;
	}

	private boolean processHelp() {
		if(this.compileValues.getHelp()) {
			System.out.println("Help on 'compile' option.");
			
			return true;
		} else 
			return false;
	}

	@Override
	public void compile() {
		
		if(processHelp())
			return;
		
		List<String> sources = compileValues.getSources();
		if(sources.isEmpty()) {
			reporter.error("No source specified.");
			return;
		}
		if(sources.size() > 1) {
			reporter.error("Script must have only one source; found " + String.join(", ", sources));
			return;
		}
		String source = sources.get(0);
		
		InputTextProvider provider = inputTextFactory.apply(source);
		
		Session session = frontEnd.createScriptSession(provider);
		if(reporter.hasErrors())
			return;
		
		for(Backend backend: backends) {
			backend.process(session);
		}
	}

}
