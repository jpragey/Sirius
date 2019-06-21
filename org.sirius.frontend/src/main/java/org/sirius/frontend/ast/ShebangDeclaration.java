package org.sirius.frontend.ast;

import org.antlr.v4.runtime.Token;

public class ShebangDeclaration implements Visitable {
	private AstToken content;

	public ShebangDeclaration(AstToken content) {
		super();
		this.content = content;
	}
	public ShebangDeclaration(Token token) {
		this(new AstToken(token));
	}
	public AstToken getContentToken() {
		return content;
	}
	
	public void visit(AstVisitor visitor) {
		visitor.startShebangDeclaration(this);
		visitor.endShebangDeclaration(this);
	}
		
}
