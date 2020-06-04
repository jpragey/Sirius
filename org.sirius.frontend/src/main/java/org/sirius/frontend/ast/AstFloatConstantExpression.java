package org.sirius.frontend.ast;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FloatConstantExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

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
	public AstType getType() {
		throw new UnsupportedOperationException("TODO");
	}

	private class FloatConstantExpressionImpl implements FloatConstantExpression {
		@Override
		public Type getType() {
			return Type.floatType;
		}
	}
	private FloatConstantExpressionImpl impl = null;

	@Override
	public Expression getExpression() {
		if(impl == null)
			impl = new FloatConstantExpressionImpl();

		return impl;
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
	public AstFloatConstantExpression linkToParentST(DefaultSymbolTable parentSymbolTable) {
		return this;
	}

	
}
