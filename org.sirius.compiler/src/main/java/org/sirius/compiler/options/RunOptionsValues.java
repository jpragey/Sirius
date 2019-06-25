package org.sirius.compiler.options;

public class RunOptionsValues {

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
