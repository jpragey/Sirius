package org.sirius.frontend.api;

public interface BooleanConstantExpression extends Expression {
	boolean value();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
