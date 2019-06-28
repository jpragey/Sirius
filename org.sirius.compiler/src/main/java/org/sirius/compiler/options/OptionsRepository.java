package org.sirius.compiler.options;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sirius.compiler.cli.framework.BooleanOption;
import org.sirius.compiler.cli.framework.BoundOption;
import org.sirius.compiler.cli.framework.CommandOption;
import org.sirius.compiler.cli.framework.Option;
import org.sirius.compiler.cli.framework.SingleArgOption;


public class OptionsRepository {
	
	public static final SingleArgOption<Help> output = new SingleArgOption<Help>(
			"-o,--output <FILE>   : output jar file",
			Set.of("-o", "--output"), 
			new Help("-o,--output <FILE>   : output jar file"));
	
	public static final BooleanOption<Help> help = new BooleanOption<>(
			"-h,--help   : print help and exit",
			Set.of("-h", "--help"), 
			new Help("-h,--help   : print help and exit")); 

	public static final BooleanOption<Help> version = new BooleanOption<>(
			"-v,--version   : print version and exit",
			Set.of("-v", "--version"), 
			new Help("-v,--version   : print version and exit")); 

	/** '--class' option (for compile command) */
	public static final	SingleArgOption<Help> classDir = new SingleArgOption<Help>(
			"--class <DIR>   : create .class files in DIR",
			Set.of("--class"), 
			new Help("--class <DIR>   : create .class files in DIR"));

	
	public static class SubCommandOption<Values> {
		public Option<Help> option;
		public Function<Values, BoundOption<Help>> binding;
		public SubCommandOption(Option<Help> option, Function<Values, BoundOption<Help>> binding) {
			super();
			this.option = option;
			this.binding = binding;
		}
		public Help getHelp() {
			return option.getHelp();
		}
		public Option<Help> getOption() {
			return option;
		}
		public Function<Values, BoundOption<Help>> getBinding() {
			return binding;
		}
	}
	public static List<SubCommandOption<CompileOptionsValues>> compileCommandOptions = Arrays.asList(
			new SubCommandOption<CompileOptionsValues>(help, (CompileOptionsValues v) -> help.bind(v::setHelp)),
			new SubCommandOption<CompileOptionsValues>(classDir, (CompileOptionsValues v) -> classDir.bind(v::setClassDir))
			);
	
	public static CommandOption<CompileOptionsValues, Help> compileCommand(RootOptionValues compilerOptionValues) {
		
		return new CommandOption<CompileOptionsValues, Help>(
			"description", 
			"compile", 
			compilerOptionValues::createCompileOptions,
			compileCommandOptions.stream()
				.map( SubCommandOption::getBinding)
				.collect(Collectors.toList()),
//			Arrays.asList(
//					(CompileOptionsValues v) -> help.bind(v::setHelp),
//					(CompileOptionsValues v) -> classDir.bind(v::setClassDir)
//					), 
			Optional.of( (CompileOptionsValues optionValues, List<String> sources) -> {
				optionValues.setSources(sources); 
				return Optional.empty();
			}),
			new Help("Compile source files to outputs according to selected backends (jvm by default).") );
	}

	public static CommandOption<RunOptionsValues, Help> runCommand(RootOptionValues compilerOptionValues) {
		
		return new CommandOption<RunOptionsValues, Help>(
			"description", 
			"run", 
			compilerOptionValues::createRunOptions, 
			Arrays.asList(
					(RunOptionsValues v) -> help.bind(v::setHelp)
					), 
			Optional.empty(),
			new Help("Run source/compiled files.") );
	}

	
	public static List<BoundOption<Help>> bindStandardCompilerOptions(RootOptionValues values) {
		
		List<BoundOption<Help>> optionParsers = Arrays.asList(
				output.bind(values::setOutput),
				help.bind(values::setHelp),
				version.bind(values::setVersion),
				compileCommand(values).bind(),
				runCommand(values).bind()
				);
		
		return optionParsers;
	}

}
