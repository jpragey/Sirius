package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;

public class ExpressionStatementImpl implements ExpressionStatement {
	private Expression expression;
	
	public ExpressionStatementImpl(Expression expression) {
		super();
		this.expression = expression;
	}

	@Override
	public Expression getExpression() {
		return expression;
	}
	@Override
	public String toString() {
		
		return "ExpressionStatementImpl: " + expression.toString();
	}
}