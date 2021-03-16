package org.sirius.frontend.api;

public interface ClassDeclaration extends ClassType {

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitContent(visitor);
		visitor.end(this);
	}
}
