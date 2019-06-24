package org.sirius.compiler.cli.framework;

public interface OptionParser {
	Boolean parseOption(CliIterator cliIterator) throws CliException;
}