package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.StringConstantExpression;

public class AstStringConstantExpression implements AstExpression {
	
	private AstToken content;

	public AstStringConstantExpression(AstToken content) {
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


	@Override
	public Expression getExpression() {
		return new StringConstantExpression() {
		};
	}
}
