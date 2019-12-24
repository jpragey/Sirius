package org.sirius.frontend.ast;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ReturnStatement;
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
		return new ReturnStatement() {
			
			@Override
			public Expression getExpression() {
				return expression.getExpression();
			}
		};
	}
	
}


