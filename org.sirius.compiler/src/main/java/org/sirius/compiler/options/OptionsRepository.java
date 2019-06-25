package org.sirius.compiler.options;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.sirius.compiler.cli.framework.BooleanOption;
import org.sirius.compiler.cli.framework.BoundOption;
import org.sirius.compiler.cli.framework.CommandOption;
import org.sirius.compiler.cli.framework.SingleArgOption;


public class OptionsRepository {
	
	public static final SingleArgOption<Help> output = new SingleArgOption<Help>(
			"-o,--output <FILE>   : output jar file",
			Set.of("-o", "--output"), 
			new Help());
	
	public static final BooleanOption<Help> help = new BooleanOption<>(
			"-h,--help   : print help and exit",
			Set.of("-h", "--help"), 
			new Help()); 

	public static final BooleanOption<Help> version = new BooleanOption<>(
			"-v,--version   : print version and exit",
			Set.of("-v", "--version"), 
			new Help()); 

	
	public static CommandOption<CompileOptionsValues, Help> compileCommand(CompilerOptionValues compilerOptionValues) {
		
		return new CommandOption<CompileOptionsValues, Help>(
			"description", 
			"compile", 
			()-> compilerOptionValues.createCompileOptions(), 
			Arrays.asList(
					(CompileOptionsValues v) -> help.bind(v::setHelp)
					), 
			new Help() );
	}

	public static CommandOption<RunOptionsValues, Help> runCommand(CompilerOptionValues compilerOptionValues) {
		
		return new CommandOption<RunOptionsValues, Help>(
			"description", 
			"run", 
			()-> compilerOptionValues.createRunOptions(), 
			Arrays.asList(
					(RunOptionsValues v) -> help.bind(v::setHelp)
					), 
			new Help() );
	}

	
	public static List<BoundOption> bindStandardCompilerOptions(CompilerOptionValues values) {
		
		List<BoundOption> optionParsers = Arrays.asList(
				output.bind(values::setOutput),
				help.bind(values::setHelp),
				version.bind(values::setVersion),
				compileCommand(values).bind(),
				runCommand(values).bind()
				);
		
		return optionParsers;
	}

}
