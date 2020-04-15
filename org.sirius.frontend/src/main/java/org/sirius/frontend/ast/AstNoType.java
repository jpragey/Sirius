package org.sirius.frontend.ast;

import org.sirius.frontend.symbols.SymbolTable;

public class AstNoType implements AstType {

	@Override
	public String messageStr() {
		return "<no type>";
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
}
