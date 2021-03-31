package org.sirius.sdk.tooling;

import java.util.Arrays;
import java.util.List;

import org.sirius.sdk.org.sirius.Addable;
import org.sirius.sdk.org.sirius.SiriusBoolean;
import org.sirius.sdk.org.sirius.SiriusFloat;
import org.sirius.sdk.org.sirius.SiriusFunction;
import org.sirius.sdk.org.sirius.SiriusInteger;
import org.sirius.sdk.org.sirius.SiriusString;
import org.sirius.sdk.org.sirius.Stringifiable;
import org.sirius.sdk.org.sirius.TopLevel;

public class Sdk {
	
	private static List<Class<?>> allClasses = Arrays.asList(
			// -- top-level
			TopLevel.class,
			
			// -- interfaces
			Addable.class,
			Stringifiable.class,
			SiriusFunction.class,
			
			// -- interfaces
			SiriusInteger.class,
			SiriusFloat.class,
			SiriusString.class,
			SiriusBoolean.class
			);
	
	public static List<Class<?>> sdkClasses() {
		return allClasses;
	}
}
