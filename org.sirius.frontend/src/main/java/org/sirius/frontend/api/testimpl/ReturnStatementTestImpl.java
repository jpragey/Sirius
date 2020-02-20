package org.sirius.frontend.api.testimpl;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ReturnStatement;

public class ReturnStatementTestImpl implements ReturnStatement {
	
	private Expression expression;
	
	
	public ReturnStatementTestImpl(Expression expression) {
		super();
		this.expression = expression;
	}


	@Override
	public Expression getExpression() {
		return expression;
	}

}
