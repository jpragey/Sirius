package org.sirius.frontend.ast;

import java.util.Optional;

public class StringConstantExpression implements Expression {
	
	private AstToken content;

	public StringConstantExpression(AstToken content) {
		super();
		this.content = content;
	}
	
	
	public AstToken getContent() {
		return content;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startStringConstant(this);
		visitor.endStringConstant(this);
	}
	@Override
	public Optional<Type> getType() {
		return Optional.empty();
	}
}
