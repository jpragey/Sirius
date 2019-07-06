package org.sirius.sdk.tooling;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Direct ancestor class or direct implemented interface */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Inherits.class)
public @interface Inherit {
	
	/** Package qname */
	String packageQName();
	
	/** Class/interface sirius simple name. */
	String name();

}
