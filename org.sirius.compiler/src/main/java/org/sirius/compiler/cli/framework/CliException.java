package org.sirius.compiler.cli.framework;

public class CliException extends Exception {

	public CliException(String message, Throwable cause) {
		super(message, cause);
	}

	public CliException(String message) {
		super(message);
	}
	
}
