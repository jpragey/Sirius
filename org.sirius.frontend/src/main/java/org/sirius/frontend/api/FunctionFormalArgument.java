package org.sirius.frontend.api;

import org.sirius.common.core.QName;

public interface FunctionFormalArgument {

	Type getType(/*Scope scope*/);
	
	QName getQName();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
