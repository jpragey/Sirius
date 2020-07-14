package org.sirius.compiler.cli.framework;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendedOptionTest {

	private List<String> parsedArgs;
	private boolean emptyOptionArg;
	
	@BeforeEach
	public void setup() {
		this.parsedArgs = new ArrayList<>();
		this.emptyOptionArg = false;
		
	}
	
	private Optional<String> parseArg(String arg) {
		if(arg.isEmpty()) {
			emptyOptionArg = true;
		} else if(arg.startsWith("=")) {
			parsedArgs = Arrays.asList(arg.substring(1) /* skip '=' */.split(","));
		} else 
			return Optional.of("Error: unexpected start (must be '=') in " + arg);
		
		return Optional.empty();
	}
	
	@Test
	public void argParsingWithArgsOK() {
		ExtendedOption<String> opt = new ExtendedOption<String>("description", "--verbose", "Some help");
		
		String[] args = {"--verbose=aa,bb,cc"};
		
		Cursor cursor = new Cursor( args);
		ArgumentParsingResult r = opt.bind(this::parseArg).parse(cursor);
		assertTrue(r.matched);
		assertThat(parsedArgs, is(List.of("aa", "bb", "cc")));
		assertThat(emptyOptionArg, is(false));
	}
	
	@Test
	public void argParsingWithoutArgsOK() {
		ExtendedOption<String> opt = new ExtendedOption<String>("description", "--verbose", "Some help");
		
		String[] args = {"--verbose"};
		
		Cursor cursor = new Cursor( args);
		ArgumentParsingResult r = opt.bind(this::parseArg).parse(cursor);
		assertTrue(r.matched);
		assertThat(parsedArgs, is(Arrays.asList()));
		assertThat(emptyOptionArg, is(true));
	}
	
	@Test
	public void argParsingWithWrongArgsFailed() {
		ExtendedOption<String> opt = new ExtendedOption<String>("description", "--verbose", "Some help");
		
		String[] args = {"--verboseX"};
		
		Cursor cursor = new Cursor( args);
		ArgumentParsingResult r = opt.bind(this::parseArg).parse(cursor);
		assertThat(r.matched, is(true));
		assertTrue(r.errorMessage.get().contains("Error"));
		assertThat(parsedArgs, is(Arrays.asList()));
		assertThat(emptyOptionArg, is(false));
	}
	
}
