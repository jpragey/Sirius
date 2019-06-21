package org.sirius.frontend.api;

public interface TopLevelFunction extends AbstractFunction {

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
