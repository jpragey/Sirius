package org.sirius.frontend.api;

import java.util.Optional;

public interface Expression {
	default void visitMe(Visitor visitor) {
		throw new UnsupportedOperationException("Unsupported yet: visitMe() for Expression : " + this);
	}
	
	// TODO: should be optional, (TODO: done) 
	Type getType();
}
