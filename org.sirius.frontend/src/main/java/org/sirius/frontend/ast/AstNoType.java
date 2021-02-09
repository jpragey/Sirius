package org.sirius.frontend.ast;

import org.sirius.frontend.api.Type;

public class AstNoType implements AstType {

	@Override
	public String messageStr() {
		return "<no type>";
	}

	@Override
	public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method.");
	}

	@Override
	public void visit(AstVisitor visitor) {
	}

	@Override
	public AstType resolve() {
		return this;
	}

	@Override
	public boolean isExactlyA(AstType type) {
		return false;
	}

	@Override
	public boolean isStrictDescendantOf(AstType type) {
		return false;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		return false;
	}

	@Override
	public String toString() {
		return "AstNoType";
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AstNoType);
	}
	
	@Override
	public int hashCode() {
		return 0x78029453;	// arbitrary
	}

	@Override
	public void verify(int featureFlags) {
		// Nothing to do
	}

}
