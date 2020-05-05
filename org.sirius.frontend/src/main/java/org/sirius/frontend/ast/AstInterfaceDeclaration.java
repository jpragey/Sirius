package org.sirius.frontend.ast;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.sirius.frontend.symbols.SymbolTable;

public class AstInterfaceDeclaration implements AstType, Scoped, Visitable, AstParametric<AstClassDeclaration>, AstClassOrInterface {

	@Override
	public Optional<AstClassDeclaration> apply(AstType parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SymbolTable getSymbolTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String messageStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visit(AstVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AstType resolve() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExactlyA(AstType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStrictDescendantOf(AstType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AstFunctionDeclaration> getFunctionDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AncestorInfo> getAncestors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AstClassDeclaration> getInterfaces() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

}
