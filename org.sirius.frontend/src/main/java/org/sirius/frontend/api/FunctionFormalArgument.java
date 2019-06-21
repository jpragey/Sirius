package org.sirius.frontend.api;

import org.sirius.common.core.QName;

public interface FunctionFormalArgument {

	QName getQName();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
