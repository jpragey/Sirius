package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.SymbolTable;

public class UnionType implements AstType{
	private AstType first;
	private AstType second;
	private Optional<UnionType> resolvedElementType = Optional.empty();

	public UnionType(AstType first, AstType second) {
		super();
		this.first = first;
		this.second = second;
	}
	public AstType getFirst() {
		return first;
	}
	public AstType getSecond() {
		return second;
	}
	
	@Override
	public String messageStr() {
		return first.messageStr() + " | " + second.messageStr();
	}
	@Override
	public boolean isExactlyA(AstType type) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean isStrictDescendantOf(AstType type) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public UnionType resolve() {
		if(resolvedElementType.isEmpty())
			resolvedElementType = Optional.of(new UnionType(first.resolve(), second.resolve()));
		
		return resolvedElementType.get();
	}
	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		first.visit(visitor);
		second.visit(visitor);
		visitor.end(this);		
	}

	@Override
	public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method.");
	}
	@Override
	public void verify(int featureFlags) {
		first.verify(featureFlags);
		second.verify(featureFlags);
		verifyOptional(resolvedElementType, "resolvedElementType", featureFlags);
	}
}
