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

	private class ReturnStatementImpl implements ReturnStatement {
		private Expression apiExpr = expression.getExpression();
		@Override
		public Expression getExpression() {
			return apiExpr;
		}
		@Override
		public String toString() {
			return apiExpr.toString();
		}
	}
	private ReturnStatementImpl impl = null;
	
	@Override
	public ReturnStatement toAPI() {
		if(impl == null)
			impl = new ReturnStatementImpl();
		
		return impl;
	}

	@Override
	public String toString() {
		return expression.toString();
	}

	@Override
	public void verify(int featureFlags) {
		expression.verify(featureFlags);
		verifyNotNull(impl, "AstReturnStatement.impl");
	}
}


