package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class AstStringConstantExpression implements AstExpression {
	
	private AstToken contentToken;
	private String contentString;
	private DefaultSymbolTable symbolTable = null;
	
	// TODO: should be static
	private AstClassDeclaration stringType;

	public AstStringConstantExpression(AstToken content) {
		super();
		this.contentToken = content;
		String text = content.getText();
		this.contentString = text.substring(1, text.length()-1);
	}
	
	
	public AstToken getContentToken() {
		return contentToken;
	}
	
	public String getContentString() {
		return contentString;
	}

	
	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		
		// TODO: Maybe we should check ??? 
		this.stringType = symbolTable.lookupByQName(new QName("sirius", "lang", "String") ).get().getClassDeclaration().get();
	}

//	@Override
//	public DefaultSymbolTable getSymbolTable() {
//		return symbolTable;
//	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startStringConstant(this);
		visitor.endStringConstant(this);
	}
	@Override
	public AstType getType() {
		return stringType;
	}

	public static class StringConstantExpressionImpl implements StringConstantExpression {
		private AstToken contentToken;
		private String contentString;
		
		public StringConstantExpressionImpl(AstToken contentToken, String contentString) {
			super();
			this.contentToken = contentToken;
			this.contentString = contentString;
		}

		@Override
		public Token getContent() {
			return contentToken.asToken();
		}

		@Override
		public String getText() {
			return contentString;
		}
		
	}
	
	private Optional<Expression> impl = null;
	
	@Override
	public Optional<Expression> getExpression() {
		if(impl == null) {
			StringConstantExpressionImpl expr = new StringConstantExpressionImpl(contentToken, contentString);
			this.impl = Optional.of(expr);
		}
		return this.impl;
	}
	
	@Override
	public String toString() {
		return "AstIntegerConstantExpression:\"" + this.contentString + "\"";
	}
	@Override
	public String asString() {
		return toString();
	}

	@Override
	public AstStringConstantExpression linkToParentST(DefaultSymbolTable parentSymbolTable) {
//		this.symbolTable = symbolTable;
//		
//		// TODO: Maybe we should check ??? 
//		this.stringType = symbolTable.lookup(new QName("sirius", "lang", "String") ).get().getClassDeclaration().get();

		return this;
	}


	@Override
	public void verify(int featureFlags) {
		verifyNotNull(symbolTable, "symbolTable");
		
		stringType.verify(featureFlags);
	}

}
