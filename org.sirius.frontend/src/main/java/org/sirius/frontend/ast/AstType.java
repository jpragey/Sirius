package org.sirius.frontend.ast;

import org.sirius.frontend.api.Type;

public interface AstType {

	public String messageStr();
	
	default public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method.");
	}
	
	public boolean isExactlyA(AstType type);
	
	public boolean isStrictDescendantOf(AstType type);
//	public boolean isStrictAncestorOf(AstType type);
	public boolean isAncestorOrSameAs(AstType type);
	
}
