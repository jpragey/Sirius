package org.sirius.sdk.tooling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Sirius.lang class */
@Retention(RetentionPolicy.RUNTIME)
public @interface TopLevelMethods {

	/** Dot-separated package full qname */
	String packageQName();
	
}
