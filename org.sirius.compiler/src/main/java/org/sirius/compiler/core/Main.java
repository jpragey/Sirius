package org.sirius.compiler.core;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.sirius.backend.jvm.BackendOptions;
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
	
	static record ParsedCommandLine(RootOptionValues optionValues, List<BoundOption<Help>> boundOptions, Optional<String> error) {		
	}
	
	public static ParsedCommandLine parseCommandLine(Reporter reporter, String[] mainArgs) {
		RootOptionValues optionValues = new RootOptionValues(reporter);
		
		List<BoundOption<Help>> boundOptions = OptionsRepository.bindStandardCompilerOptions(optionValues);
		final List<String> sourceArgs = new ArrayList<String>();
		OptionParser<RootOptionValues, Help> parser = new OptionParser<>( 
				sources -> {
					sourceArgs.addAll(sources); 
					return Optional.empty();
				},
				boundOptions) ;
		
		Optional<String> error = parser.parse(mainArgs);
		return new ParsedCommandLine(optionValues, boundOptions, error);
	}

	public static final int EXIT_OK = 0;
	public static final int EXIT_ERROR = -1;
	

	/**
	 * Phases:
	 * - create Reporter
	 * - parse command-line args; exit with error if failed;
	 * - if --help or --version, process and exit OK;
	 * - create a compiler and run it.
	 *  
	 * @param mainArgs
	 */
	public static void main(String[] mainArgs) {
		
		int exitStatus = EXIT_OK;
		Reporter reporter = new ShellReporter(); 

		ParsedCommandLine parsedCommandLine = parseCommandLine(reporter, mainArgs);

		if(parsedCommandLine.error.isPresent()) {
			reporter.error(parsedCommandLine.error.get());
			exitStatus = EXIT_ERROR;
		} else {
			RootOptionValues optionValues = parsedCommandLine.optionValues();

			if(optionValues.getHelp()) {
				HelpProcessor helpProcessor = new HelpProcessor(parsedCommandLine.boundOptions);
				helpProcessor.printHelp(reporter);
			} else if(optionValues.getVersion()) {
				new Version().printVersion(reporter);
			} else {
				try {
					runTool(reporter, optionValues);
				} catch(Exception e) {
					reporter.error("Exiting by Exception: " + e.getMessage(), e);
				}
			}
		}
		
		if(reporter.hasErrors())
			exitStatus = EXIT_ERROR;
		
		System.exit(exitStatus);
	}

	private static void runTool(Reporter reporter, RootOptionValues optionValues) {
		
		Optional<CompileOptionsValues> compileOptions = optionValues.getCompileOptions();
		
		if(compileOptions.isPresent()) {
			CompilerTool compilerTool = new CompilerTool(reporter, optionValues, compileOptions.get());
			compilerTool.runCompileTool(/*compileOptions.get()*/);
		} else {
			reporter.error("No command found in command line args.");
		}
	}
	
}
