package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
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

	private Optional<Statement> impl = null;
	
	@Override
	public Optional<Statement> toAPI() {
		if(impl == null) {
			Optional<Expression> apiExpr = expression.getExpression();
//			ReturnStatementImpl rstmt = new ReturnStatementImpl(expression);
			impl = apiExpr.map(e -> new ReturnStatementImpl(e));
		}
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


