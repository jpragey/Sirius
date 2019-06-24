package org.sirius.compiler.cli.framework;

import java.util.function.Function;


public interface Option <V, Help> {
	public OptionParser bind(Function<String, Boolean> accept);
	public Help getHelp();
}