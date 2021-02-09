package org.sirius.frontend.ast;

import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.apiimpl.ReturnStatementImpl;

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

	private ReturnStatementImpl impl = null;
	
	@Override
	public ReturnStatement toAPI() {
		if(impl == null)
			impl = new ReturnStatementImpl(expression);
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


