package org.sirius.frontend.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.apiimpl.BinaryOpExpressionImpl;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class AstBinaryOpExpression implements AstExpression {
	
	private BinaryOpExpression.Operator operator;
	
	private AstExpression left;
	private AstExpression right;
	
	private AstToken opToken;

	private Optional<Expression> impl = null;

	private static Map<String, BinaryOpExpression.Operator> opMap = new HashMap<>() {{
		for(BinaryOpExpression.Operator op: BinaryOpExpression.Operator.values())
			put(op.getSymbol(), op);
			
//		put("+", BinaryOpExpression.Operator.Add);
//		put("-", BinaryOpExpression.Operator.Substract);
//		put("*", BinaryOpExpression.Operator.Mult);
//		put("/", BinaryOpExpression.Operator.Divide);
//		put("^", BinaryOpExpression.Operator.Exponential);
//
//		put(">", BinaryOpExpression.Operator.Greater);
//		put("<", BinaryOpExpression.Operator.Lower);
//		put(">=", BinaryOpExpression.Operator.GreaterOrEqual);
//		put("<=", BinaryOpExpression.Operator.LowerOrEqual);
//
//		put("==", BinaryOpExpression.Operator.EqualEqual);
	}};
	
	private static BinaryOpExpression.Operator parseOperator(AstToken opToken) {
		String text = opToken.getText();
		BinaryOpExpression.Operator op = opMap.get(text);
		if(op == null)
			throw new RuntimeException("BinaryOpExpression: unknown operator " + text);	// TODO: better error handling
		return op;
	}
	
	public AstBinaryOpExpression(AstExpression left, AstExpression right, AstToken opToken, BinaryOpExpression.Operator operator) {
		this.operator = operator;
		this.left = left;
		this.right = right;
		this.opToken = opToken;
	}
	public AstBinaryOpExpression(AstExpression left, AstExpression right, AstToken opToken) {
		this(left, right, opToken, parseOperator(opToken));
	}
	
	public AstBinaryOpExpression(AstExpression left, AstExpression right, Token opToken) {
		this(left, right, new AstToken(opToken));
	}
		
	
	public BinaryOpExpression.Operator getOperator() {
		return operator;
	}

	public AstExpression getLeft() {
		return left;
	}

	public AstExpression getRight() {
		return right;
	}

	public AstToken getOpToken() {
		return opToken;
	}

	@Override
	public AstType getType() {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public void visit(AstVisitor visitor) {

		visitor.startBinaryOpExpression(this);
		
		left.visit(visitor);
		right.visit(visitor);
		
		visitor.endBinaryOpExpression(this);
	}

	
	@Override
	public Optional<Expression> getExpression() {
		
		if(impl == null) {
			Optional<Expression> leftExpr = left.getExpression();
			Optional<Expression> rightExpr = right.getExpression();
			if(leftExpr.isEmpty() || rightExpr.isEmpty()) {
				impl = Optional.empty();
			} else {
				Expression e = new BinaryOpExpressionImpl(leftExpr.get(), rightExpr.get(), operator);
				impl = Optional.of(e);
			}
		}
		return impl;
	}

	@Override
	public String asString() {
		return toString();
	}
	@Override
	public String toString() {
		return left.toString() + " " + operator.toString() + " " + right.toString();
	}

	@Override
	public AstExpression linkToParentST(DefaultSymbolTable parentSymbolTable) {
		AstExpression newLeft = left.linkToParentST(parentSymbolTable);
		AstExpression newRight = right.linkToParentST(parentSymbolTable);
		return new AstBinaryOpExpression(newLeft, newRight, opToken, operator);
	}

	@Override
	public void verify(int featureFlags) {
		
		left.verify(featureFlags);
		right.verify(featureFlags);
		
		verifyCachedObjectNotNull(impl, "AstBinaryOpExpression.impl", featureFlags);
	}

}
