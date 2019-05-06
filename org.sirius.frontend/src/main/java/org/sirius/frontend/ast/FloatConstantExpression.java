package org.sirius.frontend.ast;

import java.util.Optional;

public class FloatConstantExpression implements Expression {
	
	private AstToken content;

	public FloatConstantExpression(AstToken content) {
		super();
		this.content = content;
	}
	
	
	public AstToken getContent() {
		return content;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startFloatConstant(this);
		visitor.endFloatConstant(this);
	}
	@Override
	public Optional<Type> getType() {
		return Optional.empty();
	}
}
