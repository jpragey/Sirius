package org.sirius.compiler.cli.framework;

import java.util.Set;

public 	class BooleanOption<Help> implements Option<Help> {
	private String description;
	private Set<String> matchingKeywords;
	private Help help;
	
	public BooleanOption(String description, Set<String> matchingKeywords, Help help) {
		super();
		this.description = description;
		this.matchingKeywords = matchingKeywords;
		this.help = help;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public BoundOption<Help> bind(Signal signal) {
		return new BoundOption<Help>() {

			@Override
			public ArgumentParsingResult parse(Cursor cursor) {
				if(matchingKeywords.contains(cursor.lookahead())) {
					signal.apply();
					cursor.advance(1);
					return ArgumentParsingResult.success();
				}
					
				return ArgumentParsingResult.notMatched();
			}

			@Override
			public Help getHelp() {
				return help;
			}
			
		};
	}

	@Override
	public Help getHelp() {
		return help;
	}
}
