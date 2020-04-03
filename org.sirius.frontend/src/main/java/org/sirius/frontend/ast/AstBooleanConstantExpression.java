package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.BooleanConstantExpression;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.Type;

public class AstBooleanConstantExpression implements AstExpression {
	
	private AstToken content;

	public AstBooleanConstantExpression(AstToken content) {
		super();
		this.content = content;
	}
	
	
	public AstToken getContentToken() {
		return content;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startBooleanConstant(this);
		visitor.endBooleanConstant(this);
	}
	@Override
	public AstType getType() {
		return null;
	}


	@Override
	public Expression getExpression() {
		
		return new BooleanConstantExpression() {

			@Override
			public Type getType() {
				return Type.booleanType;
			}
		};
	}
}
