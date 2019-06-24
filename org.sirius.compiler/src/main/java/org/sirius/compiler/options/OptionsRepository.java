package org.sirius.compiler.options;

import java.util.Arrays;
import java.util.List;

import org.sirius.compiler.cli.framework.OptionParser;
import org.sirius.compiler.cli.framework.SingleArgOption;

public class OptionsRepository {
	
	public static final SingleArgOption<AbstractOptionValues, Help> output = new SingleArgOption<AbstractOptionValues, Help>(
			"-o", "--output", new Help()); 

	
	public static List<OptionParser> bindStandardCompilerOptions(CompilerOptionValues values) {

		List<OptionParser> optionParsers = Arrays.asList(
		
				output.bind(values::setOutput)
				
				);
		
		return optionParsers;
	}

}
