package org.sirius.compiler.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Options values for 'compile' command
 * 
 * @author jpragey
 *
 */
public class CompileOptionsValues {

//	private String moduleDir =
	/** '--help' option */
	private Boolean help = false;

	private Optional<String> classDir = Optional.empty();
	
	private ArrayList<String> sources = new ArrayList<String>();
	
	public void setHelp() {
		this.help = true;
	}

	public Boolean getHelp() {
		return help;
	}

	public Optional<String> getClassDir() {
		return classDir;
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
	
	
}
