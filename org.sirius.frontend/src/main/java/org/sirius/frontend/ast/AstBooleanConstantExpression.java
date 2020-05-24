package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.BooleanConstantExpression;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.Type;

public class AstBooleanConstantExpression implements AstExpression {
	
	private AstToken content;
	private boolean value;

	public AstBooleanConstantExpression(AstToken content) {
		super();
		this.content = content;
		this.value = content.getText().equals("true");
	}
	
	
	public AstToken getContentToken() {
		return content;
	}
	
	
	public boolean isValue() {
		return value;
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

			@Override
			public boolean getValue() {
				return value;
			}
		};
	}


	@Override
	public String asString() {
		return toString();
	}
	@Override
	public String toString() {
		return content.getText();
	}
}
