package org.sirius.frontend.api;

public interface IntegerConstantExpression extends Expression {
	int value();

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}
}
