package org.sirius.backend.jvm.launcher;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws Exception {	// TODO: manage exceptions
		Main main = new Main();
		main.run(args);
	}
	
	void printErr(String text) {
		System.err.println(text);
	}
	void printUsage() {
		System.out.println("Usage: \n" +
//				"java -cp dist/lib/ -jar dist/lib/org.sirius.runtime-0.0.1-SNAPSHOT.jar <module>\n");
				"java -cp \"../dist/lib/*:modulesDir/unnamed.jar\" org.sirius.backend.jvm.launcher.Main unused\n");

//	"java -cp dist/lib/ -jar dist/lib/org.sirius.runtime-0.0.1-SNAPSHOT.jar <module>\n");
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
	
	private void run(String[] args) throws Exception {
		RunCliOptions options = parseArg(args);
		if(!options.isOK())
			return;
		
		ClassLoader classLoader = getClass().getClassLoader();
		
		String mainClassQName = "$package$"; 
//		String mainClassQName = "A"; 
		
		Class<?> cls = classLoader.loadClass(mainClassQName);

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		
		
		Method main = cls.getMethod("main", new Class[] { /*sirius.lang.String [].class*/ });
		Object[] argTypes = new Object[] {};
		
//		System.out.println("Running main...");
		Object result = main.invoke(null, argTypes /*, args*/);
//		System.out.println("Main over: " + result.getClass().getCanonicalName());
//		if(result instanceof sirius.lang.Integer) {
//			System.out.println("Value: " + ((sirius.lang.Integer)result).getValue());
//		}

	}
	

}
