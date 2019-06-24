package org.sirius.backend.jvm;

import org.sirius.backend.core.Backend;
import org.sirius.frontend.api.Session;

public class JvmBackend implements Backend {

	@Override
	public String getBackendId() {
		return "jvm";
	}

	@Override
	public void process(Session session) {
		
	}
}
