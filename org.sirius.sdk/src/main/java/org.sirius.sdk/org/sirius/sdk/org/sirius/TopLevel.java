package org.sirius.sdk.org.sirius;

import org.sirius.sdk.bridge.PlatformBridge;
import org.sirius.sdk.tooling.Parameter;
import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelMethods;

@TopLevelMethods(packageQName = "sirius.lang")
public class TopLevel {
	
	private PlatformBridge bridge;
	
	public TopLevel(PlatformBridge bridge) {
		super();
		this.bridge = bridge;
	}


	@SiriusMethod
	public void println(
//			@Parameter(typeQName = "sirius.lang.Stringifiable")
			@Parameter(typeQName = "sirius.lang.String")
//			Stringifiable text
			SiriusString text
			) 
	{
		//System.out.println(text.string());
		this.bridge.getPrintln().println(text);
	}

	@SiriusMethod
	public void println0(
			) 
	{
		//System.out.println(text.string());
		this.bridge.getPrintln().println0();
	}

}
