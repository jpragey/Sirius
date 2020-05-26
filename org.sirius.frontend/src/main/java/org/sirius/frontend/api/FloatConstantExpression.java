package org.sirius.frontend.api;

public interface FloatConstantExpression extends Expression {
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
