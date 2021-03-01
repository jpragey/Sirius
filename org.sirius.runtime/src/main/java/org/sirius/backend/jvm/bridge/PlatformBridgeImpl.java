package org.sirius.backend.jvm.bridge;

import org.sirius.sdk.bridge.PlatformBridge;
import org.sirius.sdk.bridge.Println;
import org.sirius.sdk.org.sirius.SiriusString;

public class PlatformBridgeImpl implements PlatformBridge {

	private Println println = new Println() {
		
		@Override
		public void println(SiriusString text) {
			System.out.println("*******************************************************************************************************" + text.toString());
		}

		@Override
		public void println0() {
			System.out.println("*******************************************************************************************************0");
		}
		
	};
	@Override
	public Println getPrintln() {
		return this.println;
	}

}
