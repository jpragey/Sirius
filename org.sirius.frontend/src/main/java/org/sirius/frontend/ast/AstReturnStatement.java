package org.sirius.frontend.ast;

import org.sirius.frontend.api.Statement;

public class AstReturnStatement implements AstStatement {

	private AstExpression expression;

	public AstReturnStatement(AstExpression expression) {
		super();
		this.expression = expression;
	}

	public AstExpression getExpression() {
		return expression;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startReturnStatement(this);
		expression.visit(visitor);
		visitor.endReturnStatement(this);
	}

	@Override
	public Statement toAPI() {
		// TODO Auto-generated method stub
		return null;
	}
	
}


