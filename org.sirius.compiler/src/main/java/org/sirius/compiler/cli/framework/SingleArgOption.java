package org.sirius.compiler.cli.framework;

import java.util.function.Function;

public class SingleArgOption<V, Help> implements Option<V, Help> {
	private String shortName;
	private String longName;
	private Help help;
	
	public SingleArgOption(String shortName, String longName, Help help) {
		this.shortName = shortName;
		this.longName = longName;
		this.help = help;
	}

	@Override
	public Help getHelp() {
		return help;
	}

	@Override
	public OptionParser bind(Function<String, Boolean> accept) {
		return new SingleArgOptionParser(shortName, longName, accept);
	}
}
