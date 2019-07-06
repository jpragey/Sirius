package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.Parameter;
import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelMethods;

@TopLevelMethods(packageQName = "sirius.lang")
public class TopLevel {
	
	@SiriusMethod
	public void println(
			@Parameter(typeQName = "sirius.lang.String")
			Stringifiable text
			) 
	{
		System.out.println(text.string());
	}

}
