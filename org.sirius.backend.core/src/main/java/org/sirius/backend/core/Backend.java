package org.sirius.backend.core;

import org.sirius.frontend.api.Session;

public interface Backend {

	/** get the ID string, eg 'jvm' or 'javascript'
	 * 
	 * @return
	 */
	public String getBackendId();
	
	public void process(Session moduleContent);
	
}
