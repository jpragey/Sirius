package org.sirius.frontend.api;

import org.sirius.common.core.Token;

public interface FunctionActualArgument extends Expression {

	Token nameToken();
	/** index in argument list */
	int paramIndex();
	
	default void visitMe(Visitor visitor) {
		visitor.startFunctionActualArgument(this);
//		visitContent(visitor);
		visitor.endFunctionActualArgument(this);
	}

	
	
}
