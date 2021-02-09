package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.BooleanConstantExpression;
import org.sirius.frontend.api.Type;

public class BooleanConstantExpressionImpl implements BooleanConstantExpression {
	private boolean value;

	public BooleanConstantExpressionImpl(boolean value) {
		super();
		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.booleanType;
	}

	@Override
	public boolean getValue() {
		return value;
	}
	@Override
	public String toString() {
		return Boolean.toString(value);
	}
	
}