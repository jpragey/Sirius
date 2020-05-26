package org.sirius.frontend.api;

public interface TypeCastExpression extends Expression {
	public Expression expression();
	public Type targetType();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);

		expression().visitMe(visitor);
		
		visitor.end(this);
	}

	
}
