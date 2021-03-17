package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.apiimpl.BooleanConstantExpressionImpl;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class AstBooleanConstantExpression implements AstExpression {
	
	private AstToken content;
	private boolean value;
	private Optional<Expression> impl = null;

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
	public Optional<Expression> getExpression() {
		if(impl == null) {
			impl = Optional.of(new BooleanConstantExpressionImpl(value));
		}
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
	public AstBooleanConstantExpression linkToParentST(SymbolTable parentSymbolTable) {
		return this;
	}


	@Override
	public void verify(int featureFlags) {
		// Nothing to do
	}
}
