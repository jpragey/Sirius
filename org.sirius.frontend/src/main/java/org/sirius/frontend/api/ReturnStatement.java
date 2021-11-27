package org.sirius.frontend.api;

public interface ReturnStatement extends Statement {

	Expression expression();

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		expression().visitMe(visitor);
		visitor.end(this);
	}
	
	default Type getExpressionType() {
		return expression().type();
	}

}
