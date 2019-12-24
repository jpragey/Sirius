package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.symbols.GlobalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class IntersectionType implements AstType{
	private AstType first;
	private AstType second;
	private Optional<IntersectionType> resolvedElementType = Optional.empty();

	public IntersectionType(AstType first, AstType second) {
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
		return first.messageStr() + "&" + second.messageStr();
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
	public IntersectionType resolve(SymbolTable symbolTable) {
		if(resolvedElementType.isEmpty())
			resolvedElementType = Optional.of(new IntersectionType(first.resolve(symbolTable), second.resolve(symbolTable)));
		
		return resolvedElementType.get();
	}
	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		first.visit(visitor);
		second.visit(visitor);
		visitor.end(this);		
	}

	
}
