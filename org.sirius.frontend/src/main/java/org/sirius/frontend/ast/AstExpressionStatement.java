package org.sirius.frontend.ast;

import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.apiimpl.ExpressionStatementImpl;

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

	private ExpressionStatementImpl apiImpl = null;
	@Override
	public ExpressionStatement toAPI() {
		if(apiImpl == null)
			apiImpl = new ExpressionStatementImpl(expression.getExpression());
		return apiImpl;
	}

	@Override
	public void verify(int featureFlags) {
		expression.verify(featureFlags);
	}

}
