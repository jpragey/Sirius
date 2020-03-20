package org.sirius.frontend.ast;

import org.sirius.frontend.api.VoidType;
import org.sirius.frontend.symbols.SymbolTable;

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

	@Override
	public AstType resolve(SymbolTable symbolTable) {
		return this;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		visitor.end(this);		
	}

	
}
