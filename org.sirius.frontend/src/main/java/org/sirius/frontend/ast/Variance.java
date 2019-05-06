package org.sirius.frontend.ast;

public enum Variance {
	IN("in"), OUT("in"), INVARIANT("");
	
	private String name;

	private Variance(String name) {
		this.name = name;
	}
	
	
}
