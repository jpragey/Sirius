package org.sirius.frontend.api;

import org.sirius.common.core.Token;

public interface LocalVariableReference extends Expression {

	Token getName();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
//		visitContent(visitor);
		visitor.end(this);
	}

	
	
}
