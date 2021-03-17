package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.sdk.SdkContent;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class AstIntegerConstantExpression implements AstExpression {
	
	private AstToken content;
	private int value = 0;

	// TODO: should be static
	private AstClassDeclaration intType;
	
	private SymbolTable symbolTable; // TODO: optional (???)
	
	public AstIntegerConstantExpression(AstToken content, Reporter reporter) {
		super();
		this.content = content;
		
		String text = content.getText();
		try {
			this.value = Integer.parseInt(text);
		} catch(NumberFormatException e) {
			reporter.error("Value '" + text + "' is not a valid integer.", content, e);
		}
	}
	
	
	public AstToken getContentToken() {
		return content;
	}
	
	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		
		this.intType = symbolTable
				.lookupByQName(SdkContent.siriusLangIntegerQName) // new QName("sirius", "lang", "Integer") )
				.get()
				.getClassDeclaration()
				.get();	// NoSuchElementException if not found
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startIntegerConstant(this);
		visitor.endIntegerConstant(this);
	}
	@Override
	public AstType getType() {
		return this.intType;
	}
	
	public int getValue() {
		return value;
	}

	private class IntegerConstantExpressionImpl implements IntegerConstantExpression {
		private int value;
		
		public IntegerConstantExpressionImpl(int value) {
			super();
			this.value = value;
		}

		@Override
		public int getValue() {
			return value;
		}

		@Override
		public Type getType() {
			return Type.integerType;
		}

		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}
	
	private Optional<Expression> impl = null;
	
	@Override
	public Optional<Expression> getExpression() {
		if(impl == null)
			impl = Optional.of(new IntegerConstantExpressionImpl(value));
		return impl;
	}
	@Override
	public String toString() {
		return "AstIntegerConstantExpression:" + this.value;
	}
	@Override
	public String asString() {
		return toString();
	}

	@Override
	public AstIntegerConstantExpression linkToParentST(SymbolTable parentSymbolTable) {
		return this;
	}


	@Override
	public void verify(int featureFlags) {
		// Nothing to do
	}
}
