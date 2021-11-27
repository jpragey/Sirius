package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.BooleanConstantExpression;
import org.sirius.frontend.api.Type;

public record BooleanConstantExpressionImpl(boolean value) implements BooleanConstantExpression {

	@Override
	public Type getType() {
		return Type.booleanType;
	}

	@Override
	public String toString() {
		return Boolean.toString(value);
	}
	
}