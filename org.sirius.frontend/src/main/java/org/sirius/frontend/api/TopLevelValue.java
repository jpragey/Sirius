package org.sirius.frontend.api;

public interface TopLevelValue extends AbstractValue {

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
