package org.sirius.compiler.cli.framework;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.sirius.compiler.options.Help;
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
	
	@Test
	public void simpleOptionsTestTest() {
		OptionsValue optionsValue = new OptionsValue();

		BooleanOption<Help> boolOption0 = new BooleanOption<Help>("description", Set.of("-b0"), new Help(""));
		BooleanOption<Help> boolOption1 = new BooleanOption<Help>("description", Set.of("-b1"), new Help(""));
		
		SingleArgOption<Help> outputDirOption = new SingleArgOption<Help>("Output dir (", Set.of("-o", "--outdir"), new Help("help"));
		
		CommandOption<CommandOptionsValue, Help> commandOption = new CommandOption<>("description", "command", 
				() -> optionsValue.createCommand(), 
				Arrays.asList(
						(CommandOptionsValue v) -> outputDirOption.bind(dir -> {v.outputDir = dir;})
				), 
				Optional.empty(),
				new Help("help"));
		
		String[] cliArgs = {"-b0", "-b1", "command", "-o", "target"};
		OptionParser<OptionsValue, Help> parser = new OptionParser<OptionsValue, Help>(
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
