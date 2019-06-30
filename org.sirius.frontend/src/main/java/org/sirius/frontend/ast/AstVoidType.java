package org.sirius.frontend.ast;

import org.sirius.frontend.api.VoidType;

public class AstVoidType implements AstType {

	@Override
	public String messageStr() {
		return "void";
	}

	@Override
	public VoidType getApiType() {
		return new VoidType() {};
	}

}
