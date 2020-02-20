package org.sirius.frontend.api.testimpl;

import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.Visitor;

public class IntegerConstantExpressionTestImpl implements IntegerConstantExpression {
	private int value;
	
	public IntegerConstantExpressionTestImpl(int value) {
		super();
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

	@Override
	public Type getType() {
		return Type.integerType;
	}

}
