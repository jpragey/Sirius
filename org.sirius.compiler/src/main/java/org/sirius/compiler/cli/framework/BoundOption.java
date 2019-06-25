package org.sirius.compiler.cli.framework;

public interface BoundOption<Help> {
	
	ArgumentParsingResult parse(Cursor cursor);
	Help getHelp();
}
