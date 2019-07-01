package org.sirius.frontend.api;

public interface Statement {

	default void visitMe(Visitor visitor) {
		visitor.start(this);
//		visitContent(visitor);
		visitor.end(this);
	}

}
