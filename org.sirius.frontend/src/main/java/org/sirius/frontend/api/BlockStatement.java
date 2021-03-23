package org.sirius.frontend.api;

public interface BlockStatement extends Statement {


	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}
}
