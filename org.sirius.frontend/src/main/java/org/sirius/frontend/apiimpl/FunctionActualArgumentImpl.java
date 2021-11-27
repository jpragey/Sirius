package org.sirius.frontend.apiimpl;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.FunctionActualArgument;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstFunctionParameter;

public class FunctionActualArgumentImpl implements FunctionActualArgument {
	private Type type;
	private Token name;
	private int paramIndex;
	public FunctionActualArgumentImpl(AstFunctionParameter param) {
		this.type = param.getType().getApiType();
		this.name = param.getName().asToken();
		this.paramIndex = param.getIndex();
	}
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Token getName() {
		return name;
	}
	@Override
	public String toString() {
		return "arg: " + type.toString() + " " + name.getText();
	}

	@Override
	public int getIndex() {
		return paramIndex;
	}
}