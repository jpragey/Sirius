package org.sirius.frontend.ast;

import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.GlobalSymbolTable;

public interface ResolvedType {

	public String messageStr();
	
	default public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method.");
	}
	
	public boolean isExactlyA(ResolvedType type);
	
	public boolean isStrictDescendantOf(ResolvedType type);
//	public boolean isStrictAncestorOf(AstType type);
	public boolean isAncestorOrSameAs(ResolvedType type);
	
}
