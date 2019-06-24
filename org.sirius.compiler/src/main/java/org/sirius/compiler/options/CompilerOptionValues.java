package org.sirius.compiler.options;

import org.sirius.common.error.Reporter;

public class CompilerOptionValues implements AbstractOptionValues {

	private Reporter reporter; 
	
	private String output = "./modules";

	
	public CompilerOptionValues(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

//
//	public CompilerOptionValues bindStandardOptions() {
//		return this;
//	}
	
//	public static void addCompilerOptions(CompilerOptionValues values) {
//		
//		OptionsRepository.output.bind(values::setOutput);
//	}
	
	
	@Override
	public String getOutput() {
		return output;
	}

	public boolean setOutput(String output) {
		this.output = output;
		return true;
	}

	
}
