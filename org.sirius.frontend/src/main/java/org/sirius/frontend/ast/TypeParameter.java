package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Type;

public class TypeParameter implements AstType {

	private Variance variance;
	
	private AstToken formalName;
	
	private Optional<AstType> defaultType;
	

	public TypeParameter(Variance variance, AstToken formalName, Optional<AstType> defaultType) {
		super();
		this.variance = variance;
		this.formalName = formalName;
		this.defaultType = defaultType;
	}
	public TypeParameter(Variance variance, AstToken formalName) {
		this(variance, formalName, Optional.empty());
	}
	
	public Variance getVariance() {
		return variance;
	}

	public AstToken getFormalName() {
		return formalName;
	} 
	
	public String getNameString() {
		return formalName.getText();
	} 
	
	public Optional<AstType> getDefaultType() {
		return defaultType;
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
	
	// TODO: implement yet
	@Override
	public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method (yet).");
	}

}
