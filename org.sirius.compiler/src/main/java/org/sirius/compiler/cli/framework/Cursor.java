package org.sirius.compiler.cli.framework;

import java.util.Arrays;
import java.util.List;

public 	class Cursor {
	private String[] cliArgs;
	private int currentPos;

	public Cursor(String[] cliArgs) {
		super();
		this.cliArgs = cliArgs;
	}
	public String lookahead(int offset) {return cliArgs[currentPos + offset] ;}
	public String lookahead() {return lookahead(0) ;}
	public void advance(int count) {currentPos += count;}
	public Boolean exists(int offset) {return currentPos + offset < cliArgs.length;}
	public int getCurrentPos() {return currentPos;}
	public Boolean hasMoreElements() {return currentPos < cliArgs.length;}
	public List<String> remainingArgs() {return Arrays.asList(cliArgs).subList(currentPos, cliArgs.length);}
}

