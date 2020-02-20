package org.sirius.frontend.api;

public interface ReturnStatement extends Statement {

	Expression getExpression();

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getExpression().visitMe(visitor);
		visitor.end(this);
	}
	
	default Type getExpressionType() {
		return getExpression().getType();
	}

}
