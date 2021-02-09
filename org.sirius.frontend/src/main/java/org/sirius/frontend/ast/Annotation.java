package org.sirius.frontend.ast;

public class Annotation implements Verifiable {

	private AstToken name;

	public Annotation(AstToken name) {
		super();
		this.name = name;
	}

	public AstToken getName() {
		return name;
	}

	@Override
	public void verify(int featureFlags) {
		// Nothing to do
	}

	
}
