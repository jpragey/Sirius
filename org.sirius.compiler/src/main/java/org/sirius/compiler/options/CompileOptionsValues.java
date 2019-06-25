package org.sirius.compiler.options;

/** Options values for 'compile' command
 * 
 * @author jpragey
 *
 */
public class CompileOptionsValues {

//	private String moduleDir =
	/** '--help' option */
	private Boolean help = false;

	public void setHelp() {
		this.help = true;
	}

	public Boolean getHelp() {
		return help;
	}
	
	
}
