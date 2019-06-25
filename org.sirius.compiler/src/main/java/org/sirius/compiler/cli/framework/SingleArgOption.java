package org.sirius.compiler.cli.framework;

import java.util.Set;
import java.util.function.Consumer;

public class SingleArgOption<Help> implements Option<Help> {
	private String description;
	private Set<String> matchingKeywords;
	private Help help;

	public SingleArgOption(String description, Set<String> matchingKeywords, Help help) {
		super();
		this.description = description;
		this.matchingKeywords = matchingKeywords;
		this.help = help;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public BoundOption<Help> bind(Consumer<String> setter) {
		return new BoundOption<Help>() {

			@Override
			public ArgumentParsingResult parse(Cursor cursor) {
				String lookahead = cursor.lookahead();
				if(matchingKeywords.contains(lookahead)) {
					if(cursor.exists(1)) {
						setter.accept(cursor.lookahead(1));
						cursor.advance(2 /*matchedArgCount*/);
						return ArgumentParsingResult.success();
					} else {
						return ArgumentParsingResult.fail("Option " + lookahead + " needs an argument.");
					}
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
