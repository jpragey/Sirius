package org.sirius.compiler.cli.framework;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** Option with optional embedded values, eg --verbose[=flags]
 * 
 * @author jpragey
 *
 * @param <Help>
 */
public class ExtendedOption<Help> implements Option<Help> {
	private String description;
	private String keywordRoot;
	private Help help;
//	private Function<String, Optional<String>> processArg;
//	private Supplier<Optional<String>> processNoArg;

	public ExtendedOption(String description, String keywordRoot, 
//			Function<String, Optional<String>> processArg,
//			Supplier<Optional<String>> processNoArg,
			Help help) {
		super();
		this.description = description;
		this.keywordRoot = keywordRoot;
//		this.processArg = processArg;
//		this.processNoArg = processNoArg;
		this.help = help;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @param processArg called when matched, argument is everything after the keyword 
	 * @param processNoArg
	 * @return
	 */
	public BoundOption<Help> bind(Function<String, Optional<String>> processArg) {
		return new BoundOption<Help>() {

			@Override
			public ArgumentParsingResult parse(Cursor cursor) {
				String lookahead = cursor.lookahead();
				if(lookahead.startsWith(keywordRoot)) {
					String extra = lookahead.substring(keywordRoot.length());
					Optional<String> error = processArg.apply(extra);
					
					cursor.advance(1 /*matchedArgCount*/);
					if(error.isPresent())
						return ArgumentParsingResult.fail(error.get());
					
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
