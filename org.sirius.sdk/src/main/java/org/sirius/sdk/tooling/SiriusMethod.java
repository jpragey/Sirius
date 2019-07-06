package org.sirius.sdk.tooling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SiriusMethod {

	String methodName() default ""; 
}
