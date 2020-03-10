package org.sirius.backend.jvm.launcher;

import java.util.Optional;

public class RunCliOptions {

	private Optional<String> moduleName;
	private String[] appArgs = {};
	
	public RunCliOptions() {
		super();
		this.moduleName = Optional.empty();
	}

	public Optional<String> getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = Optional.of(moduleName);
	}
	
	public boolean isOK() {
		return moduleName.isPresent();
	}

	public String[] getAppArgs() {
		return appArgs;
	}

	public void setAppArgs(String[] appArgs) {
		this.appArgs = appArgs;
	}
	
}
