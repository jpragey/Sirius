package org.sirius.compiler.cli.framework;

import java.util.Optional;


/** Result of a bound option parsing:
 * - notMatched: not matched at all, try next bound option;
 * - success: matched, eventual args parsed, no error, try next argument; matchedArgCount tells how many CLI args to skip
 * - failed: matched, but argument parsing failed; errorMessage tells the reason; abort CLI parsing
 */
public class ArgumentParsingResult {
	/** Nb of matched CLI args */
//	int matchedArgCount;
	Optional<String> errorMessage;
	boolean matched;	// success or failed
	public ArgumentParsingResult(/*int matchedArgCount, */Optional<String> errorMessage, boolean matched) {
		super();
//		this.matchedArgCount = matchedArgCount;
		this.errorMessage = errorMessage;
		this.matched = matched;
	}
	public static ArgumentParsingResult success(/*int matchedArgCount*/) {return new ArgumentParsingResult(/*matchedArgCount, */Optional.empty(), true);}
	public static ArgumentParsingResult fail(String message) {return new ArgumentParsingResult(/*0, */Optional.of(message), true);}
	public static ArgumentParsingResult notMatched() {return new ArgumentParsingResult(/*0, */Optional.empty(), false);}
	// Success or failed
	public boolean isMatched() {return matched;}
	public Optional<String> hasError() {return errorMessage;}
}
