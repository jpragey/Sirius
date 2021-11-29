package org.sirius.frontend.apiimpl;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.StringConstantExpression;

public record StringConstantExpressionImpl(Token content) implements StringConstantExpression {
	
	@Override
	public Token getContent() {
		return content;
	}

	@Override
	public String getText() {
		return content.getText();
	}
}
