package org.sirius.frontend.ast;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.Token;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.Visitor;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class AstBinaryOpExpression implements AstExpression {
	
	private BinaryOpExpression.Operator operator;
	
	private AstExpression left;
	private AstExpression right;
	
	private AstToken opToken;
	
	private static Map<String, BinaryOpExpression.Operator> opMap = new HashMap<>() {{
		put("+", BinaryOpExpression.Operator.Add);
		put("-", BinaryOpExpression.Operator.Substract);
		put("*", BinaryOpExpression.Operator.Mult);
		put("/", BinaryOpExpression.Operator.Divide);
		put("^", BinaryOpExpression.Operator.Exponential);
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

	class BinaryOpExpressionImpl implements BinaryOpExpression {

		@Override
		public Expression getLeft() {
			return left.getExpression();
		}

		@Override
		public Expression getRight() {
			return right.getExpression();
		}

		private boolean isInteger(Type t) {
			return true;
		}
		@Override
		public Type getType() {
			Type leftType = left.getExpression().getType();
			Type rightType = right.getExpression().getType();
			
			if(leftType == Type.integerType && rightType == Type.integerType ) {
				return Type.integerType;
			}
			
			throw new UnsupportedOperationException("Partial support of getType() in AstBinaryOpExpression yet (only integer are supported)");
		}

		@Override
		public void visitMe(Visitor visitor) {
			visitor.start(this);
			getLeft().visitMe(visitor);
			getRight().visitMe(visitor);
			visitor.end(this);
		}
		@Override
		public String toString() {
			return left.getExpression() + " " + operator + " " + right.getExpression();
		}

		@Override
		public Operator getOperator() {
			return operator;
		}
	};
	private BinaryOpExpressionImpl impl = null;
	
	@Override
	public Expression getExpression() {
		
		if(impl == null)
			impl = new BinaryOpExpressionImpl();
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

}
