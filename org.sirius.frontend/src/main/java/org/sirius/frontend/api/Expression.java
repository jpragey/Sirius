package org.sirius.frontend.api;

public interface Expression {
	default void visitMe(Visitor visitor) {
		throw new UnsupportedOperationException("Unsupported yet: visitMe() for Expression : " + this);
	}
	Type getType();
}
