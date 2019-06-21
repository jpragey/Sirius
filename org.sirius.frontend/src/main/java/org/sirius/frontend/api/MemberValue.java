package org.sirius.frontend.api;

public interface MemberValue extends AbstractValue {
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
