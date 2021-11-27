package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ReturnStatement;

public record ReturnStatementImpl(Expression expression) implements ReturnStatement {

	@Override
	public String toString() {
		return expression.toString();
	}
}