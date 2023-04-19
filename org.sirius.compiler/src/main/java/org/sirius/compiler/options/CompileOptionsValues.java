package org.sirius.compiler.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Options values for 'compile' command
 * 
 * @author jpragey
 *
 */
public class CompileOptionsValues {

	/** '--help' option */
	private Boolean help = false;

	private Optional<String> classDir = Optional.empty();

	private Optional<String> moduleDir = Optional.empty();

	private Optional<String> jvmMain = Optional.empty();

	private ArrayList<String> sources = new ArrayList<String>();

	// Builder pattern, facilitate tests
	public static class Builder {
		CompileOptionsValues cov = new CompileOptionsValues();
		public Builder() {}
		public Builder withHelp(Boolean help) {cov.help = help; return this;}
		public Builder withClassDir(String classDir) {cov.classDir = Optional.of(classDir); return this;}
		public Builder withModuleDir(String moduleDir) {cov.moduleDir = Optional.of(moduleDir); return this;}
		public Builder withJvmMain(String jvmMain) {cov.jvmMain = Optional.of(jvmMain); return this;}
		public Builder addSource(String source) {cov.sources.add(source); return this;}
		public CompileOptionsValues get() {return cov;}
	}
	
	public void setHelp() {
		this.help = true;
	}

	public Boolean getHelp() {
		return help;
	}

	public Optional<String> getClassDir() {
		return classDir;
	}

	public Optional<String> getModuleDir() {
		return moduleDir;
	}

	public void setModuleDir(String moduleDir) {
		this.moduleDir = Optional.of(moduleDir);
	}

	public void setClassDir(String classDir) {
		this.classDir = Optional.of(classDir);
	}

	public ArrayList<String> getSources() {
		return sources;
	}

	public void setSources(List<String> sources) {
		this.sources.addAll(sources);
	}

	public void setJvmMain(String jvmMain) {
		this.jvmMain = Optional.of(jvmMain);
	}
	public Optional<String> getJvmMain() {
		return jvmMain;
	}



	private boolean verboseAst = false;

	private void setAllVerboseoptions() {
		verboseAst=true;
	}
	public Optional<String> setVerbose(String flagString) {
		if(flagString.isEmpty()) {
			setAllVerboseoptions();
		} else if(flagString.startsWith("=")) {
			for(String flag : Arrays.asList(flagString.substring(1) /* skip '=' */.split(","))) {
				switch(flag) {
				case "all": 
					setAllVerboseoptions();
					break;
				case "ast": 
					verboseAst=true;
					break;
				default: return Optional.of("Unknown '--verbose' flag " + flag);
				}
			}
		} else 
			return Optional.of("Error: unexpected start (must be '=') in " + flagString);
		
		return Optional.empty();

	}

	/** TODO : not used any more ??? */
	public boolean isVerboseAst() {
		return verboseAst;
	}
	
	
}
