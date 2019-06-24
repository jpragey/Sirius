package org.sirius.compiler.cli.framework;

import java.util.Optional;
import java.util.function.Function;

class SingleArgOptionParser implements OptionParser {
	private Function<String, Boolean> matcher;
	private Function<String, Boolean> accept;

	public SingleArgOptionParser(Function<String, Boolean> matcher, Function<String, Boolean> accept) {
		super();
		this.matcher = matcher;
		this.accept = accept;
	}
	public SingleArgOptionParser(String name, Function<String, Boolean> accept) {
		this( (String arg) -> name==arg, accept);
	}
	public SingleArgOptionParser(String shortName, String longName, Function<String, Boolean> accept) {
		this( (String arg) -> shortName==arg || longName==arg, accept);
	}
	@Override
	public Boolean parseOption(CliIterator cliIterator) throws CliException {
		Optional<String> optName = cliIterator.getLookahead(0);
		if(!optName.isPresent())
			return false;
		
		String name = optName.get();
		if(!matcher.apply(name))
			return false;
		
		Optional<String> optValue = cliIterator.getLookahead(1);
		if(!optValue.isPresent())
			throw new CliException("Option " + name +" requires one argument.");
		
		String value = optValue.get();
		if(accept.apply(value)) {
			cliIterator.advance(2);
			return true;
		}
		return false;
	}
	
}