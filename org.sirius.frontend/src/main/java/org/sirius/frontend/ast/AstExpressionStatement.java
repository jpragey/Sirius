package org.sirius.frontend.ast;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.Statement;

public class AstExpressionStatement implements AstStatement {

	private AstExpression expression;
	
	public AstExpressionStatement(AstExpression expression) {
		super();
		this.expression = expression;
	}

	public AstExpression getExpression() {
		return expression;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startExpressionStatement(this);
		expression.visit(visitor);
		visitor.endExpressionStatement(this);
	}

	private class ExpressionStatementImpl implements ExpressionStatement {
		
		@Override
		public Expression getExpression() {
			return expression.getExpression();
		}
	};

	private ExpressionStatementImpl apiImpl = null;
	@Override
	public ExpressionStatement toAPI() {
		if(apiImpl == null)
			apiImpl = new ExpressionStatementImpl();
		return apiImpl;
	}

	@Override
	public void verify(int featureFlags) {
		expression.verify(featureFlags);
	}

}
