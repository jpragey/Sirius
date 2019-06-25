package org.sirius.compiler.cli.framework;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class OptionParser<V, Help> {
	
	 /** Remaining args handler that fails if remaining args list is not empty. 
	  * 
	  * @param remainingArgs
	  * @return
	  */
	private static Optional<String> failingRemainingHandler(List<String> remainingArgs) {
		if(remainingArgs.isEmpty())
			return Optional.empty(); 
		return Optional.of("Unexpected extra arguments : " + remainingArgs); 
	}
	
	private Function<List<String>, Optional<String> > remainingHandler ;
	private List<BoundOption<Help>> boundOptions;

	
	public OptionParser(Function<List<String>, Optional<String>> remainingHandler,
			List<BoundOption<Help>> boundOptions) {
		super();
		this.remainingHandler = remainingHandler;
		this.boundOptions = boundOptions;
	}
	public OptionParser(List<BoundOption<Help>> boundOptions) {
		this(OptionParser::failingRemainingHandler, boundOptions);
	}
	
	@SafeVarargs
	public OptionParser(BoundOption<Help> ... boundOptions) {
		this(OptionParser::failingRemainingHandler, Arrays.asList(boundOptions));
	}
	
	
	/** Return error message */
	public Optional<String> parse(String[] cliArgs) {
		return parse(new Cursor(cliArgs));
	}
	public Optional<String> parse(Cursor cursor) {
		
		while(cursor.hasMoreElements()) {	// Loop on arg list
			int currentPos = cursor.getCurrentPos();
			boolean optionMatched = false;
			
			for(BoundOption<Help> bo: boundOptions) {
				ArgumentParsingResult parsedCount = bo.parse(cursor);
				if(parsedCount.matched) {	// Success or failed
					Optional<String> errorMessage = parsedCount.errorMessage;
					if(errorMessage.isPresent()) {
						return errorMessage;
					} else { // parsing OK
//						cursor.advance(parsedCount.matchedArgCount);
						optionMatched = true;
						break;
					}
					
				} else {
					
				}
			}
			
			if(!optionMatched) {
				List<String> remainigArgs = cursor.remainingArgs();
				Optional<String> result = remainingHandler.apply(remainigArgs);
				return result;
			}
			if(currentPos == cursor.getCurrentPos()) {	// No advance
				return Optional.of("No match for argument " + cursor.getCurrentPos() + ": " + cursor.lookahead());
			}
		}
		return Optional.empty();
	}
}
