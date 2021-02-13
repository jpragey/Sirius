package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.FloatConstantExpression;
import org.sirius.frontend.api.Type;

public class FloatConstantExpressionImpl implements FloatConstantExpression {
	@Override
	public Type getType() {
		return Type.floatType;
	}
}