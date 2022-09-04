package org.sirius.frontend.ast;

import org.antlr.v4.runtime.Token;

public class ShebangDeclaration implements Visitable {
	private AstToken content;
	/** text withou '#!' and trimmed*/
	private String trimmedText;

	public ShebangDeclaration(AstToken content) {
		super();
		this.content = content;
		String txt = content.getText();
		this.trimmedText = txt.substring(2, txt.length()).trim();
	}
	public ShebangDeclaration(Token token) {
		this(new AstToken(token));
	}
	public AstToken getContentToken() {
		return content;
	}
	
	public String getTrimmedText() {
		return trimmedText;
	}
	@Override
	public String toString() {
		return trimmedText;
	}
	public void visit(AstVisitor visitor) {
		visitor.startShebangDeclaration(this);
		visitor.endShebangDeclaration(this);
	}
		
}
