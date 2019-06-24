package org.sirius.compiler.cli.framework;

import java.util.List;

public class OptionsRunner/*<V>*/ {
	List<OptionParser> options;
//	V values;
	public OptionsRunner(/* values */  List<OptionParser> options) {
		super();
		this.options = options;
//		this.values = values;
	}
	public void parse(String[] cliArgs) throws CliException {
		CliIterator cliIterator = new CliIterator(cliArgs);
		
		int remaining = cliIterator.remainingSize();
		while(remaining > 0) {

			for(OptionParser parser: options) {
				if(parser.parseOption(cliIterator))
					break;
			}
			
			int r = cliIterator.remainingSize();
			if(r == remaining) {
				throw new CliException("Invalid extra arguments: " + cliIterator.remainingAsString());
			}
			remaining = r;
		}
	}
}