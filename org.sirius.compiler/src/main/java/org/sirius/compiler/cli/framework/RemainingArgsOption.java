package org.sirius.compiler.cli.framework;

import java.util.Optional;
import java.util.function.Function;

/** Catches everything up to the end of arguments; stop if the argument is not accepted.
 * 
 * @author jpragey
 *
 */
public class RemainingArgsOption<V, Help> implements Option<V, Help>{

	private Help help;
	
	public RemainingArgsOption(Help help) {
		super();
		this.help = help;
	}

	@Override
	public Help getHelp() {
		return help;
	}

	static class BoundOption implements OptionParser {
		private Function<String, Boolean> accept;
		
		public BoundOption(Function<String, Boolean> accept) {
			super();
			this.accept = accept;
		}

		@Override
		public Boolean parseOption(CliIterator cliIterator) throws CliException {
			Optional<String > s = cliIterator.getLookahead(0);
			while(s.isPresent()) {
				String text = s.get();
				
				Boolean accepted = accept.apply(text);
				if(!accepted)
					return false;
				
				cliIterator.advance(1);
				s = cliIterator.getLookahead(0);
			}
			
			return true;
		}
		
	}
	
	@Override
	public OptionParser bind(Function<String, Boolean> accept) {
		return new BoundOption(accept);
	}

}
