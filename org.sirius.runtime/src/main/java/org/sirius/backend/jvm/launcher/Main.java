package org.sirius.backend.jvm.launcher;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		Main main = new Main();
		main.run(args);
	}
	
	void printErr(String text) {
		System.err.println(text);
	}
	void printUsage() {
		System.out.println("Usage: \n" +
				"java -cp dist/lib/ -jar dist/lib/org.sirius.runtime-0.0.1-SNAPSHOT.jar <module>\n");
	}
	
	RunCliOptions parseArg(String[] args) {
		RunCliOptions options = new RunCliOptions();
		
		int argPos=0;
		if(args.length == 0) {
			printUsage();
		} else {
			options.setModuleName(args[0]);
			argPos++;
		}
		
		// -- Application args
		String[] appArgs = Arrays.asList(args).subList(argPos, args.length).toArray(new String[0]);
		options.setAppArgs(appArgs);
		
		return options;
	}
	
	private void run(String[] args) {
		RunCliOptions options = parseArg(args);
		if(!options.isOK())
			return;
		
		
	}
	

}
