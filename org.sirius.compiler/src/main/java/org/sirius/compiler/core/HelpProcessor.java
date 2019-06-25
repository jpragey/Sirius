package org.sirius.compiler.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.compiler.cli.framework.BoundOption;
import org.sirius.compiler.options.Help;

public class HelpProcessor {

	private static String helpPrefixFileName = "/helpPrefix.txt";
	private static String helpPostfixFileName = "/helpPostfix.txt";
	
	private List<BoundOption<Help>> boundOptions;

	public HelpProcessor(List<BoundOption<Help>> boundOptions) {
		super();
		this.boundOptions = boundOptions;
	}
	
	public void printResourceContent(String resourcePath) {
		InputStream is = Version.class.getResourceAsStream(resourcePath);
		BufferedReader breader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF8")));
		breader.lines().forEach(this::println);
	}

	private void println(String text) {
		System.out.println(text);
	}
	private void print(String text) {
		System.out.print(text);
	}

	
	
	public void printHelp(/** For error handling only */ Reporter reporter) {

		printResourceContent(helpPrefixFileName);
		println("");
		
		for(BoundOption<Help> bo: boundOptions) {
			Help help = bo.getHelp();
			println(help.getText());
			println("");
		}
		
		printResourceContent(helpPostfixFileName);
	}
	
	
}
