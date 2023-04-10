package org.sirius.backend.jvm;

public interface Constants {

	/** JVM backend ID - used to differentiate backends 
	 * @see org.sirius.backend.jvm.JvmBackend#getBackendId()
	 * */
	public static final String BACKEND_ID = "jvm";
	
	/** Version of sdk runtime, etc*/
	public static String SIRIUS_ARTEFACTS_VERSION = "0.0.1-SNAPSHOT";
	
	public static String SIRIUS_RUNTIME_VERSION = SIRIUS_ARTEFACTS_VERSION;
	public static String SIRIUS_SDK_VERSION = SIRIUS_ARTEFACTS_VERSION;

	/** Executable class execute() function name */
	public static String SIRIUS_EXECCLASS_EXEC_FUNC_NAME = "$sir$execute$";
}
