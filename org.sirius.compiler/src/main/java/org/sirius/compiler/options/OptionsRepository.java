package org.sirius.compiler.options;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.sirius.compiler.cli.framework.BooleanOption;
import org.sirius.compiler.cli.framework.BoundOption;
import org.sirius.compiler.cli.framework.CommandOption;
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
	 
	public static CommandOption<CompileOptionsValues, Help> compileCommand(RootOptionValues compilerOptionValues) {
		
		return new CommandOption<CompileOptionsValues, Help>(
			"description", 
			"compile", 
			compilerOptionValues::createCompileOptions, 
			Arrays.asList(
					(CompileOptionsValues v) -> help.bind(v::setHelp),
					(CompileOptionsValues v) -> classDir.bind(v::setClassDir)
					), 
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
