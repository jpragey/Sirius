package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.FloatConstantExpression;
import org.sirius.frontend.api.Type;

public record FloatConstantExpressionImpl() implements FloatConstantExpression {
	@Override
	public Type getType() {
		return Type.floatType;
	}
}