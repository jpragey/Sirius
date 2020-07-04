package org.sirius.frontend.api;

public interface BinaryOpExpression extends Expression {
	public enum Operator {
		Add("+"), Substract("-"), Mult("*"), Divide("/")
	, Exponential("^"),
	Greater(">"), 
	GreaterOrEqual(">="), 
	Lower("<"), 
	LowerOrEqual("<="), 
	EqualEqual("==") ,
	NotEqual("!="),
	
	AndAnd("&&"),
	OrOr("||"),
	Equal("="),
	PlusEqual("+="),
	MinusEqual("-="),
	MultEqual("*="),
	DivideEqual("/="),
	;
		
	private String symbol;

	private Operator(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}
	
	};

	Expression getLeft();
	Expression getRight();
	Operator getOperator();
}
