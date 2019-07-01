package org.sirius.frontend.api;

public interface ExpressionStatement extends Statement {

	Expression getExpression();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
//		visitContent(visitor);
		visitor.end(this);
	}

}
