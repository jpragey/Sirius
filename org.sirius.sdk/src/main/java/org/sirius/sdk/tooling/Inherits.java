package org.sirius.sdk.tooling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Direct ancestor class or direct implemented interface */
@Retention(RetentionPolicy.RUNTIME)
public @interface Inherits {
	
	Inherit [] value();
}
