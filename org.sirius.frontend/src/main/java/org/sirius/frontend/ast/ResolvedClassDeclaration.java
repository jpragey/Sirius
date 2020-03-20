package org.sirius.frontend.ast;

import org.sirius.frontend.api.Type;

public class ResolvedClassDeclaration implements ResolvedType {

	private AstClassDeclaration classDeclaration;
	
	public ResolvedClassDeclaration(AstClassDeclaration classDeclaration) {
		super();
		this.classDeclaration = classDeclaration;
	}

	public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method.");
	}

	@Override
	public String messageStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExactlyA(ResolvedType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStrictDescendantOf(ResolvedType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAncestorOrSameAs(ResolvedType type) {
		// TODO Auto-generated method stub
		return false;
	}

}
