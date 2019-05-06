package org.sirius.frontend.ast;

import java.util.Optional;

public class BooleanConstantExpression implements Expression {
	
	private AstToken content;

	public BooleanConstantExpression(AstToken content) {
		super();
		this.content = content;
	}
	
	
	public AstToken getContent() {
		return content;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startBooleanConstant(this);
		visitor.endBooleanConstant(this);
	}
	@Override
	public Optional<Type> getType() {
		return Optional.empty();
	}
}
