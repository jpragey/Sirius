package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;

public record ExpressionStatementImpl(Expression expression) implements ExpressionStatement {
	@Override
	public String toString() {
		
		return "ExpressionStatementImpl: " + expression.toString();
	}
}