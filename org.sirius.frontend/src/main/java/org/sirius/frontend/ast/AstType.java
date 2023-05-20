package org.sirius.frontend.ast;

import org.sirius.frontend.api.Type;

public interface AstType extends Verifiable {

	AstNoType noType = new AstNoType();
	
	public String messageStr();
	
	public Type getApiType();

	public void visit(AstVisitor visitor);

	public AstType resolve();
	
	
	public boolean isExactlyA(AstType type);
	
	public boolean isStrictDescendantOf(AstType type);
	public boolean isAncestorOrSameAs(AstType type);
	
}
