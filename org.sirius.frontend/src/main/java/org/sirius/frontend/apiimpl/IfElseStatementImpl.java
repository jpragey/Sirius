package org.sirius.frontend.apiimpl;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.Statement;

public class IfElseStatementImpl implements IfElseStatement {
	Expression apiIfExpression;
	Statement apiIfStatement;
	Optional<Statement> apiElseStatement;
	
	public IfElseStatementImpl(Expression apiIfExpression, Statement apiIfStatement,
			Optional<Statement> apiElseStatement) {
		super();
		this.apiIfExpression = apiIfExpression;
		this.apiIfStatement = apiIfStatement;
		this.apiElseStatement = apiElseStatement;
	}

	@Override
	public Expression getExpression() {
		return apiIfExpression;
	}

	@Override
	public Statement getIfStatement() {
		return apiIfStatement;
	}

	@Override
	public Optional<Statement> getElseStatement() {
		return apiElseStatement;
	}
	
}