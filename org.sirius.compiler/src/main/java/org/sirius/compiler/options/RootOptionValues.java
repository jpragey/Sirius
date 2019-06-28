package org.sirius.compiler.options;

import java.util.Optional;

import org.sirius.common.error.Reporter;

public class RootOptionValues implements AbstractOptionValues {

	private Reporter reporter; 
	
	private String output = "./modules";
	private Boolean help = false;		// --help is present
	private Boolean version = false;	// -- version is present
	
	private Optional<CompileOptionsValues> compileOptions = Optional.empty();
	private Optional<RunOptionsValues> runOptions = Optional.empty();
	
	
	public RootOptionValues(Reporter reporter) {
		super();
		this.reporter = reporter;
	}
	
	@Override
	public String getOutput() {
		return output;
	}

	public boolean setOutput(String output) {
		this.output = output;
		return true;
	}

	@Override
	public Boolean getHelp() {
		return help;
	}

	@Override
	public Boolean getVersion() {
		return version;
	}

	public void setHelp() {
		this.help = true;
	}

	public void setVersion() {
		this.version = true;
	}

	public Optional<CompileOptionsValues> getCompileOptions() {
		return compileOptions;
	}
	public CompileOptionsValues createCompileOptions() {
		CompileOptionsValues v = new CompileOptionsValues();
		this.compileOptions = Optional.of(v);
		return v;
	}

	public Optional<RunOptionsValues> getRunOptions() {
		return runOptions;
	}
	
	public RunOptionsValues createRunOptions() {
		RunOptionsValues v = new RunOptionsValues();
		this.runOptions = Optional.of(v);
		return v;
	}

	
}
