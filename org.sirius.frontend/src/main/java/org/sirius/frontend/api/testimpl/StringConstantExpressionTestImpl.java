package org.sirius.frontend.api.testimpl;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.Visitor;

public class StringConstantExpressionTestImpl implements StringConstantExpression {
	private Token value;
	
	public StringConstantExpressionTestImpl(Token value) {
		super();
		this.value = value;
	}

	@Override
	public void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

	@Override
	public Token getContent() {
		return value;
	}

	@Override
	public String getText() {
		return value.getText();
	}

}
