package org.sirius.frontend.core;

/** Provide text for 1 compilation unit (ie for 1 input file)
 * 
 * @author jpragey
 *
 */
public interface InputTextProvider {
	
	/** Input file content
	 * 
	 * @return
	 */
	String getText();
	
	/** Get String for input (file) location, eg file path. For (error) messages. 
	 * 
	 * @return
	 */
	String getInputLocation();

	/** package name, '/' separated, eg 'sirius/lang' */
	String getPackagePhysicalName();
	
	
	default LogicalPath getPackageLogicalPath() {
		return LogicalPath.fromPhysical(getPackagePhysicalPath());
	}
	default PhysicalPath getPackagePhysicalPath() {
		return PhysicalPath.parse(getPackagePhysicalName());
	}
	
	/** 'file' name, eg 'Object.sirius' */
	String getResourcePhysicalName();
	
	
	default Boolean isModuleDescriptor() {
		return "module.sirius" == getResourcePhysicalName();
	}
	
	default Boolean isPackageDescriptor() {
		return "package.sirius" == getResourcePhysicalName();
	}
}
