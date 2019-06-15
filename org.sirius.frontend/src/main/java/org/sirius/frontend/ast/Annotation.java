package org.sirius.frontend.ast;

public class Annotation {

	private AstToken name;

	public Annotation(AstToken name) {
		super();
		this.name = name;
	}

	public AstToken getName() {
		return name;
	}

	
}
