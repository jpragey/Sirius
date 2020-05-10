package org.sirius.frontend.api;

public interface InterfaceDeclaration extends ClassOrInterface {

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitContent(visitor);
		visitor.end(this);
	}

}
