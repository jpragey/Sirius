package org.sirius.frontend.ast;

public class UnionType implements AstType{
	private AstType first;
	private AstType second;
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

}
