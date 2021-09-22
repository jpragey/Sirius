package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.Type;

public class IntegerConstantExpressionImpl implements IntegerConstantExpression {
	int value;
	
	public IntegerConstantExpressionImpl(int value) {
		super();
		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.integerType;
	}

	@Override
	public int getValue() {
		return value;
	}

}
