package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.Type;

public record IntegerConstantExpressionImpl(int value) implements IntegerConstantExpression {

	@Override
	public Type type() {
		return Type.integerType;
	}
}
