package org.sirius.backend.jvm.bridge;

import org.sirius.sdk.org.sirius.SiriusString;

public class TopLevel {

//	public static void println(SiriusString text) {
		public static void println(sirius.lang.String text) {
//			System.out.println("*******************************************************************************************************" + text.getJvmValue());
			System.out.println(text.getJvmValue());
	}
//	public static void println0() {
//		System.out.println("*******************************************************************************************************0");
//	}

}
