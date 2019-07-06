package org.sirius.frontend.api;

public interface BinaryOpExpression extends Expression {
	public enum Operator {Add, Substract, Mult, Divide};

	Expression getLeft();
	Expression getRight();
}
