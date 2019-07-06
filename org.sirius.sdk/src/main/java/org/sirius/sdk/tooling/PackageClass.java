package org.sirius.sdk.tooling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** A PackageClass - annotated class represents a container for top-level Sirius functions and values. 
 * 
 * @author jpragey
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PackageClass {

	/** Dot-separated class full qname */
	String packageQName();
}
