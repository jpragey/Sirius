package org.sirius.compiler.cli.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.compiler.options.CompileOptionsValues;
import org.sirius.compiler.options.Help;
import org.sirius.compiler.options.OptionsRepository;
import org.sirius.compiler.options.RootOptionValues;

public class ShellArgParsingTest {

	@Test
	public void jvmMainOptionTest() {
		Reporter reporter = new ShellReporter(); 

		RootOptionValues optionValues = new RootOptionValues(reporter);

		List<BoundOption<Help>> boundOptions = OptionsRepository.bindStandardCompilerOptions(optionValues);
		
		final List<String> sourceArgs = new ArrayList<String>();
		OptionParser<RootOptionValues, Help> parser = new OptionParser<>( 
				sources -> {
					sourceArgs.addAll(sources); 
					return Optional.empty();
				},
				boundOptions) ;
		
		String[] args = {"compile", "--main", "a.b.m"};
		Optional<String> error = parser.parse(args);
		assert(error.isEmpty());
		
		Optional<CompileOptionsValues> optCompileOptionValues = optionValues.getCompileOptions();
		assert(optCompileOptionValues.isPresent());
		
		Optional<String> jvmMain = optCompileOptionValues.get().getJvmMain();
		assertThat(jvmMain.get(), is("a.b.m"));

		assertThat(reporter.ok(), is(true));
	}

}
