package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.apiimpl.BooleanConstantExpressionImpl;
import org.sirius.frontend.symbols.DefaultSymbolTable;

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
	
	
	public boolean getValue() {
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
		return new BooleanConstantExpressionImpl(value);
	}

	@Override
	public String asString() {
		return toString();
	}
	@Override
	public String toString() {
		return content.getText();
	}


	@Override
	public AstBooleanConstantExpression linkToParentST(DefaultSymbolTable parentSymbolTable) {
		return this;
	}


	@Override
	public void verify(int featureFlags) {
		// Nothing to do
	}
}
