package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IntegerConstantExpression;

public class AstIntegerConstantExpression implements AstExpression {
	
	private AstToken content;

	public AstIntegerConstantExpression(AstToken content) {
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


	@Override
	public Expression getExpression() {
		return new IntegerConstantExpression() {
		} ;
	}
}
