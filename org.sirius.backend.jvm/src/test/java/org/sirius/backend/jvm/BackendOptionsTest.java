package org.sirius.backend.jvm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.SilentReporter;

public class BackendOptionsTest {

	private AccumulatingReporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new SilentReporter());
	}

	@Test
	@DisplayName("When Main Function Name is absent, no Reporter error is generated and the option should be empty.")
	public void WhenMainFunctionNameIsAbsent_IsOK() {
		
		BackendOptions options = new BackendOptions(reporter, Optional.empty());
		
		assertThat(reporter.getErrorCount(), is(0));
		assertThat(reporter.ok(), is(true));
		assertThat(options.getJvmMainFunctionQName().isEmpty(), is(true));
	}

	@Test
	@DisplayName("When Main Function Name is absent, no Reporter error is generated and the option should be empty.")
	public void wWhenMainFunctionNameIsEmpty_ReportError() {
		
		BackendOptions options = new BackendOptions(reporter, Optional.of(""));
		
		assertThat(reporter.getErrorCount(), is(1));
		assertThat(reporter.ok(), is(false));
		assertThat(options.getJvmMainFunctionQName().isEmpty(), is(true));
	}

	@Test
	@DisplayName("When Main Function Name is present, parse it.")
	public void whenMainFunctionNameIsPresent_ParseIt() {
		
		BackendOptions options = new BackendOptions(reporter, Optional.of("aa.bb.cc"));
		
		assertThat(reporter.ok(), is(true));
		assertThat(options.getJvmMainFunctionQName().get(), is(new QName("aa", "bb", "cc")));
	}
	
}
