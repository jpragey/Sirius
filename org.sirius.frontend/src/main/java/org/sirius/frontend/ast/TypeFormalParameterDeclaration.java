package org.sirius.frontend.ast;

public class TypeFormalParameterDeclaration implements Type {

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
	
	
}
