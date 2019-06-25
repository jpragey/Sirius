package org.sirius.compiler.cli.framework;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

public class OptionParserTest {

	class CommandOptionsValue {
		public String outputDir = "";
		@Override
		public String toString() {
			return "outputDir=" + outputDir;
		}
	}
	class OptionsValue {
		public boolean b0 = false;
		public boolean b1 = false;
		public Optional<CommandOptionsValue> command = Optional.empty();
		public CommandOptionsValue createCommand() {
			CommandOptionsValue v = new CommandOptionsValue();
			command = Optional.of(v);
			return v;
		}
		@Override
		public String toString() {
			return "b0=" + b0 + ", b1=" + b1 + ", " + ", command:" + (command.isPresent() ? command.toString() : "<none>");
		}
	}
	
//	class CommandOption<CmdV, Help> implements Option<Help>{
//		String commandName;
//		Help help;
//		String description;
//		Supplier<CmdV> commandValueSupplier;
//		List< Function<CmdV, BoundOption> > bindings; 
//		
//		public CommandOption(String description, String commandName, Supplier<CmdV> commandValueSupplier, List< Function<CmdV, BoundOption> > bindings, Help help) {
//			this.commandName = commandName;
//			this.commandValueSupplier = commandValueSupplier;
//			this.bindings = bindings;
//			this.help = help;
//			this.description = description;
//		}
//
//		@Override		public String getDescription()	{ return description;}
//		@Override		public Help getHelp() 			{ return help;}
//		public BoundOption bind() {
//			return new BoundOption() {
//
//				@Override
//				public ArgumentParsingResult parse(Cursor cursor) {
//					if(cursor.lookahead().equals(commandName)) {
//						CmdV commandValue = commandValueSupplier.get();
//						List<BoundOption> boundOptions = bindings.stream()
//								.map(binding -> binding.apply(commandValue))
//								.collect(Collectors.toList());
//						
//						
//						OptionParser<CmdV> commandParser = new OptionParser<CmdV>(boundOptions);
//						cursor.advance(1); // skip command name
//						
//						Optional<String> error = commandParser.parse(cursor);
//						if(error.isPresent()) {
//							return ArgumentParsingResult.fail(error.get());
//						}
//						return ArgumentParsingResult.success(666);
//					}
//					return ArgumentParsingResult.notMatched();
//				}
//				
//			};
//		}
//	}
	
	@Test
	public void simpleOptionsTestTest() {
		OptionsValue optionsValue = new OptionsValue();

		BooleanOption<String> boolOption0 = new BooleanOption<String>("description", Set.of("-b0"), "");
		BooleanOption<String> boolOption1 = new BooleanOption<String>("description", Set.of("-b1"), "");
		
		SingleArgOption<String> outputDirOption = new SingleArgOption<>("Output dir (", Set.of("-o", "--outdir"), "help");
		Function<CommandOptionsValue, BoundOption> bind = (CommandOptionsValue v) -> outputDirOption.bind(dir -> {v.outputDir = dir;});
		
		CommandOption<CommandOptionsValue, String> commandOption = new CommandOption<>("description", "command", 
				() -> optionsValue.createCommand(), 
				Arrays.asList(
						(CommandOptionsValue v) -> outputDirOption.bind(dir -> {v.outputDir = dir;})
//						bind
				), 
				"help");
		
		String[] cliArgs = {"-b0", "-b1", "command", "-o", "target"};
		OptionParser<OptionsValue> parser = new OptionParser<OptionsValue>(
				boolOption0.bind( () -> optionsValue.b0 = true),
				boolOption1.bind( () -> optionsValue.b1 = true),
				commandOption.bind()
				);
		Optional<String> errMsg = parser.parse(cliArgs);
		
		assertFalse(errMsg.isPresent());
		
		assertEquals(optionsValue.b0, true);
		assertEquals(optionsValue.b1, true);
		CommandOptionsValue commandOptionsValue = optionsValue.command.get();
		assertEquals(commandOptionsValue.outputDir, "target");
		
	}
}
