package org.sirius.compiler.core;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.compiler.cli.framework.BoundOption;
import org.sirius.compiler.cli.framework.OptionParser;
import org.sirius.compiler.options.CompileOptionsValues;
import org.sirius.compiler.options.Help;
import org.sirius.compiler.options.OptionsRepository;
import org.sirius.compiler.options.RootOptionValues;
import org.sirius.frontend.core.FileInputTextProvider;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;

public class Main {

	private Reporter reporter;
	private RootOptionValues optionValues;
	
	public Main(Reporter reporter, RootOptionValues optionValues) {
		super();
		this.reporter = reporter;
		this.optionValues = optionValues;
	}

	public static void main(String[] args) {
		
		Reporter reporter = new ShellReporter(); 

		RootOptionValues optionValues = new RootOptionValues(reporter);
		
		List<BoundOption<Help>> boundOptions = OptionsRepository.bindStandardCompilerOptions(optionValues);
		final List<String> sourceArgs = new ArrayList<String>();
		OptionParser<RootOptionValues, Help> parser = new OptionParser<>( 
				sources -> {
					sourceArgs.addAll(sources); 
					return Optional.empty();
				},
				boundOptions) ;
		
		Optional<String> error = parser.parse(args);
		if(error.isPresent()) {
			reporter.error(error.get());
		} else {
			if(optionValues.getHelp()) {
				HelpProcessor helpProcessor = new HelpProcessor(boundOptions);
				helpProcessor.printHelp(reporter);
			} else if(optionValues.getVersion()) {
				new Version().printVersion(reporter);
			} else {
				new Main(reporter, optionValues).runTool();
//				System.out.println("Some compilation here...");
			}
		}
		
		int exitStatus = 0;
		if(reporter.hasErrors())
			exitStatus = -1;
		
		System.exit(exitStatus);
	}
	
	private void runTool() {
		
		Optional<CompileOptionsValues> compileOptions = optionValues.getCompileOptions();
		
		if(compileOptions.isPresent()) {
			runCompileTool(compileOptions.get());
		} else {
			reporter.error("No command found in command line args.");
		}
	}

	private InputTextProvider createScriptInput(String source) {
		return new FileInputTextProvider(reporter, 
				new File("."), //rootDirectory, 
				"", //packagePhysicalName, 
				source, //resourcePhysicalName, 
				Charset.forName("UTF8"));
	}
	
	private void runCompileTool(CompileOptionsValues compileOptions) {
		
		FrontEnd frontEnd = new FrontEnd(reporter);
		JvmBackend backend = new JvmBackend(reporter, 
//				compileOptions.getClassDir(), 
				compileOptions.isVerboseAst());
		
		compileOptions.getModuleDir().ifPresent(moduleDir -> backend.addFileOutput(moduleDir, compileOptions.getClassDir()));
		
		ScriptCompiler compiler = new ScriptCompiler(reporter, 
				Arrays.asList(backend), 
				frontEnd,
				optionValues,
				compileOptions,
				this::createScriptInput
				);
		compiler.compile();
	}
	
}
