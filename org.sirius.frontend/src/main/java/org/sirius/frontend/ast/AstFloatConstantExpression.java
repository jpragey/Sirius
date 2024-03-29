package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.apiimpl.FloatConstantExpressionImpl;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class AstFloatConstantExpression implements AstExpression {
	
	private AstToken content;

	private Optional<Expression> impl = null;

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

	@Override
	public Optional<Expression> getExpression() {
		if(impl == null)
			impl = Optional.of(new FloatConstantExpressionImpl());

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
	public AstFloatConstantExpression linkToParentST(SymbolTable parentSymbolTable) {
		return this;
	}

	
	public double getValue() { // TODO: check syntax
		return Double.parseDouble(content.getText());
	}


	@Override
	public void verify(int featureFlags) {
		// Nothing to do
	}
}
