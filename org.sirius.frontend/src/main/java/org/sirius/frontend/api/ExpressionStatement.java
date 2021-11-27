package org.sirius.frontend.api;

public interface ExpressionStatement extends Statement {

	Expression expression();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
//		visitContent(visitor);
		visitor.end(this);
	}

}
