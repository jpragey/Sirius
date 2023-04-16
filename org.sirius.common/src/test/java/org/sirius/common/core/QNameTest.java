package org.sirius.common.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.SilentReporter;

public class QNameTest {

	private AccumulatingReporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new SilentReporter());
	}

//	@BeforeEach
//	void setUp() throws Exception {
//	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	//////////////////////////////////////////////////

	/**
	 * - empty string (error)
	 * - simple name
	 * - a.b.c
	 * - invalid characters (incl spaces inside elements)
	 * - spaces around elements
	 */
	@Test
	@DisplayName("parseAndValidate : Empty string : error")
	public void parseAndValidate_emptyString_failsTest() {
		Optional<QName> qn = QName.parseAndValidate("", reporter);
		assertThat(qn.isEmpty(), is(true));
		assertThat(reporter.getErrorCount(), is(1));
	}

	@Test
	@DisplayName("parseAndValidate : Invalid character(s) causes an error")
	public void parseAndValidate_invalidChar_failsTest() {
		Optional<QName> qn = QName.parseAndValidate("€", reporter);
		assertThat(qn.isEmpty(), is(true));
		assertThat(reporter.getErrorCount(), is(1));
	}

	public static record ValidQNames(String dotSeparated, String[] elements) {}
	
	private static Stream<ValidQNames> parseAndValidate_validQNames /*validQNames*/() {
			return Stream.of(
					new ValidQNames("a", new String[]{"a"}),
					new ValidQNames("simpleName", new String[]{"simpleName"}),
					new ValidQNames("azAZ09_$", new String[]{"azAZ09_$"}),	// All valid chars
					new ValidQNames("a.b.c", new String[]{"a", "b", "c"}),
					new ValidQNames(" a . b . c ", new String[]{"a", "b", "c"}),	// Space around chars
					new ValidQNames("\t\ta\t\t.\t\tb\t\t.\t\tc\t\t", new String[]{"a", "b", "c"}),	// Tabs around chars
					new ValidQNames("\ta\t", new String[]{"a"})	// Tabs around chars
					);
	}
	@ParameterizedTest
	@MethodSource
	@DisplayName("parseAndValidate : valid qnames")
	public void parseAndValidate_validQNames(ValidQNames validQName) {
		Optional<QName> qn = QName.parseAndValidate(validQName.dotSeparated, reporter);
		assertThat(qn.isPresent(), is(true));
		assertThat(qn.get().getStringElements(), is(List.of(validQName.elements)));
		assertThat(reporter.getErrorCount(), is(0));
	}

}
