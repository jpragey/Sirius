package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
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

	private Optional<Statement> apiImpl = null;
	@Override
	public Optional<Statement> toAPI() {
		if(apiImpl == null) {
			Optional<Expression> e = expression.getExpression();
//			apiImpl = new ExpressionStatementImpl(e.get());
			apiImpl = e.map(expr -> new ExpressionStatementImpl(expr));
		}
		return apiImpl;
	}

	@Override
	public void verify(int featureFlags) {
		expression.verify(featureFlags);
	}

}
