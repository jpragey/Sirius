package org.sirius.frontend.ast;

import org.sirius.frontend.api.Type;

public interface AstType {

	public String messageStr();
	
	default public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method.");
	}
	
}
