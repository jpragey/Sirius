package org.sirius.frontend.api;

public interface TypeCastExpression extends Expression {
	public Expression expression();
	public Type targetType();
}
