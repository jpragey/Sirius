package org.sirius.backend.jvm;

import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassType;

public class Util {

	public static String classInternalName(QName classQName) {
		String in = classQName.getStringElements().stream().collect(Collectors.joining("/"));
		return in;
	}
	public static String classInternalName(ClassType classDeclaration) {
		/* From doc:
		 * Returns the internal name of the class corresponding to this object or array type. 
		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
		 * This method should only be used for an object or array type.
		 */
		return classInternalName(classDeclaration.qName());
	}
	public static boolean debugMainClass = false;
	
	// TODO: move
	public static final String jvmPackageClassName = "JvmPackage";	// TODO: should be "$package$"
	/** JVM backend maps s.l.Integer to ints (false) or classes (true) */
	public static final Boolean mapIntsToClasses = true;
	public static final String jvmModuleVersion = "0.0.1-SNAPSHOT";
}
