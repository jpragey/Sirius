package org.sirius.compiler.cli.framework;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ExtendedOptionTest {

	private List<String> parsedArgs;
	private boolean emptyOptionArg;
	
	@BeforeMethod
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
		assertEquals(parsedArgs, Arrays.asList("aa", "bb", "cc"));
		assertEquals(emptyOptionArg, false);
	}
	
	@Test
	public void argParsingWithoutArgsOK() {
		ExtendedOption<String> opt = new ExtendedOption<String>("description", "--verbose", "Some help");
		
		String[] args = {"--verbose"};
		
		Cursor cursor = new Cursor( args);
		ArgumentParsingResult r = opt.bind(this::parseArg).parse(cursor);
		assertTrue(r.matched);
		assertEquals(parsedArgs, Arrays.asList());
		assertEquals(emptyOptionArg, true);
	}
	
	@Test
	public void argParsingWithWrongArgsFailed() {
		ExtendedOption<String> opt = new ExtendedOption<String>("description", "--verbose", "Some help");
		
		String[] args = {"--verboseX"};
		
		Cursor cursor = new Cursor( args);
		ArgumentParsingResult r = opt.bind(this::parseArg).parse(cursor);
		assertEquals(r.matched, true);
		assertTrue(r.errorMessage.get().contains("Error"));
		assertEquals(parsedArgs, Arrays.asList());
		assertEquals(emptyOptionArg, false);
	}
	
}
