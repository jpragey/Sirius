package org.sirius.frontend.api;

public interface IntegerConstantExpression extends Expression {
	int getValue();

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}
}
