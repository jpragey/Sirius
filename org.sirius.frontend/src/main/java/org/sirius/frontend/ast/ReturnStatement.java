package org.sirius.frontend.ast;

public class ReturnStatement implements Statement {

	private Expression expression;

	public ReturnStatement(Expression expression) {
		super();
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startReturnStatement(this);
		expression.visit(visitor);
		visitor.endReturnStatement(this);
	}
	
}


