package org.sirius.compiler.cli.framework;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommandOption<CommandValues, Help> implements Option<Help>{
	private String commandName;
	private Help help;
	private String description;
	private Supplier<CommandValues> commandValueSupplier;
	private List< Function<CommandValues, BoundOption<Help>> > bindings; 
	
	public CommandOption(String description, String commandName, Supplier<CommandValues> commandValueSupplier, List< Function<CommandValues, BoundOption<Help>> > bindings, Help help) {
		this.commandName = commandName;
		this.commandValueSupplier = commandValueSupplier;
		this.bindings = bindings;
		this.help = help;
		this.description = description;
	}

	@Override		
	public String getDescription()	{ 
		return description;
	}
	@Override
	public Help getHelp() {
		return help;
	}

	public BoundOption<Help> bind() {
		return new BoundOption<Help>() {

			@Override
			public ArgumentParsingResult parse(Cursor cursor) {
				if(cursor.lookahead().equals(commandName)) {
					CommandValues commandValue = commandValueSupplier.get();
					
					List<BoundOption<Help>> boundOptions = bindings.stream()
							.map(binding -> binding.apply(commandValue))
							.collect(Collectors.toList());
					
					
					OptionParser<CommandValues, Help> commandParser = new OptionParser<>(boundOptions);
					cursor.advance(1); // skip command name
					
					Optional<String> error = commandParser.parse(cursor);
					if(error.isPresent()) {
						return ArgumentParsingResult.fail(error.get());
					}
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
}
