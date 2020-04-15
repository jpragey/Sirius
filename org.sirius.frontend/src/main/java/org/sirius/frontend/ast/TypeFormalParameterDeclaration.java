package org.sirius.frontend.ast;

import org.sirius.frontend.symbols.SymbolTable;

public class TypeFormalParameterDeclaration implements AstType {

	private Variance variance;
	
	private AstToken formalName;

	public TypeFormalParameterDeclaration(Variance variance, AstToken formalName) {
		super();
		this.variance = variance;
		this.formalName = formalName;
	}

	public Variance getVariance() {
		return variance;
	}

	public AstToken getFormalName() {
		return formalName;
	} 
	
	public String messageStr() {
		String txt = formalName.getText();
		return (variance == Variance.INVARIANT) ? txt :  variance.name() + " " + txt;
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
	public AstType resolve() {
		throw new UnsupportedOperationException("Unsupported TypeFormalParameter.resolve() for " + formalName.getText());
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		visitor.end(this);		
	}
	
}
