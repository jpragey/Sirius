package org.sirius.frontend.ast;

import java.util.Optional;

public class IntegerConstantExpression implements Expression {
	
	private AstToken content;

	public IntegerConstantExpression(AstToken content) {
		super();
		this.content = content;
	}
	
	
	public AstToken getContent() {
		return content;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startIntegerConstant(this);
		visitor.endIntegerConstant(this);
	}
	@Override
	public Optional<Type> getType() {
		return Optional.empty();
	}
}
