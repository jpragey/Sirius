package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class AstIntegerConstantExpression implements AstExpression {
	
	private AstToken content;
	private int value = 0;

	// TODO: should be static
	private AstClassDeclaration intType;
	
	private DefaultSymbolTable symbolTable; // TODO: optional (???)
	
	public AstIntegerConstantExpression(AstToken content, Reporter reporter) {
		super();
		this.content = content;
//		this.intType = AstClassDeclaration.newClass(reporter, AstToken.internal("Integer"));
//		this.intType.setqName(new QName("sirius", "lang", "Integer"));
		
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
	
	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		
		// TODO: Maybe we should check ??? 
		this.intType = symbolTable.lookup(new QName("sirius", "lang", "Integer") ).get().getClassDeclaration().get();
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startIntegerConstant(this);
		visitor.endIntegerConstant(this);
	}
	@Override
	public AstType getType() {
		return intType;
	}

	private class IntegerConstantExpressionImpl implements IntegerConstantExpression {
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
	
	private IntegerConstantExpressionImpl impl = null;
	
	@Override
	public Expression getExpression() {
		if(impl == null)
			impl = new IntegerConstantExpressionImpl();
		return impl;
	}
	@Override
	public String toString() {
		return "AstIntegerConstantExpression:" + this.value;
	}
}
