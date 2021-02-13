package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.ast.AstExpression;

public class ReturnStatementImpl implements ReturnStatement {
	private Expression apiExpr;
	public ReturnStatementImpl(Expression expression) {
		this.apiExpr = expression;
	}
//	public ReturnStatementImpl(AstExpression expression) {
//		this(expression.getExpression());
//	}
	@Override
	public Expression getExpression() {
		return apiExpr;
	}
	@Override
	public String toString() {
		return apiExpr.toString();
	}
}