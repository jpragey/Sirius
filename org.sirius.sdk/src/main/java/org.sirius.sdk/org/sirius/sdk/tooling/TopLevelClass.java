package org.sirius.sdk.tooling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Sirius.lang class or interface */
@Retention(RetentionPolicy.RUNTIME)
public @interface TopLevelClass {

	/** Dot-separated package full qname */
	String packageQName();
	
	/** Class/interface sirius simple name. If empty, the java class name will be used. */
	String name() default "";
}
