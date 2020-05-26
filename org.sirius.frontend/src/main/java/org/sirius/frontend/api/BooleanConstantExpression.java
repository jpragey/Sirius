package org.sirius.frontend.api;

public interface BooleanConstantExpression extends Expression {
	boolean getValue();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
