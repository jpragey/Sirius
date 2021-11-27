package org.sirius.frontend.apiimpl;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.FunctionActualArgument;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstFunctionParameter;

public record FunctionActualArgumentImpl(Type type, Token nameToken, int paramIndex) implements FunctionActualArgument {

	public FunctionActualArgumentImpl(AstFunctionParameter param) {
		this(param.getType().getApiType(), param.getName().asToken(), param.getIndex());
	}
	
	@Override
	public String toString() {
		return "arg: " + type.toString() + " " + nameToken.getText();
	}
}