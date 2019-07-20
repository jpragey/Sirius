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

	@Override
	public boolean isExactlyA(AstType type) {
		return type instanceof AstVoidType;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		return isExactlyA(type);
	}

	@Override
	public boolean isStrictDescendantOf(AstType type) {
		return false;
	}

}
