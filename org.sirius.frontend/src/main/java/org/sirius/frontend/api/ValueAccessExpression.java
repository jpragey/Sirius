package org.sirius.frontend.api;

public interface ValueAccessExpression extends Expression {

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getContainerExpression().visitMe(visitor);
		visitor.end(this);
	}

	Expression getContainerExpression();
	
	MemberValue getMemberValue();
}
