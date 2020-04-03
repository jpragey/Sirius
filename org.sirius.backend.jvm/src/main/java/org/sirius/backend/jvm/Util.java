package org.sirius.backend.jvm;

import java.util.stream.Collectors;

import org.sirius.frontend.api.ClassDeclaration;

public class Util {

	public static String classInternalName(ClassDeclaration classDeclaration) {
		/* From doc:
		 * Returns the internal name of the class corresponding to this object or array type. 
		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
		 * This method should only be used for an object or array type.
		 */
		String in = classDeclaration.getQName().getStringElements().stream().collect(Collectors.joining("/"));
		return in;
	}

}
