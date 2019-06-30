package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FloatConstantExpression;

public class AstFloatConstantExpression implements AstExpression {
	
	private AstToken content;

	public AstFloatConstantExpression(AstToken content) {
		super();
		this.content = content;
	}
	
	
	public AstToken getContentToken() {
		return content;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startFloatConstant(this);
		visitor.endFloatConstant(this);
	}
	@Override
	public Optional<AstType> getType() {
		return Optional.empty();
	}


	@Override
	public Expression getExpression() {
		return new FloatConstantExpression() {
		};
	}
	
}
