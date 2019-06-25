package org.sirius.compiler.core;

import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.compiler.cli.framework.BoundOption;
import org.sirius.compiler.cli.framework.OptionParser;
import org.sirius.compiler.options.CompilerOptionValues;
import org.sirius.compiler.options.Help;
import org.sirius.compiler.options.OptionsRepository;

public class Main {

	public static void main(String[] args) {
		int exitStatus = 0;
		
		Reporter reporter = new ShellReporter(); 

		CompilerOptionValues optionValues = new CompilerOptionValues(reporter);
		
		List<BoundOption<Help>> boundOptions = OptionsRepository.bindStandardCompilerOptions(optionValues);
		OptionParser<CompilerOptionValues, Help> parser = new OptionParser<>(boundOptions);
		
		Optional<String> error = parser.parse(args);
		if(error.isPresent()) {
			reporter.error(error.get());
			exitStatus = -1;
		} else {
			if(optionValues.getHelp()) {
				HelpProcessor helpProcessor = new HelpProcessor(boundOptions);
				helpProcessor.printHelp(reporter);
			} else if(optionValues.getVersion()) {
				new Version().printVersion(reporter);
			} else {
				System.out.println("Some compilation here...");
			}
		}

		CompilerBuilder builder = new CompilerBuilder(reporter);
		builder.addJvmBackend();
		builder.setCliArs(args);
		
		builder.buildScript();
		
		System.exit(exitStatus);
	}
}
