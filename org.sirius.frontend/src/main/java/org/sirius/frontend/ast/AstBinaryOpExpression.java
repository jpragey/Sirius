package org.sirius.frontend.ast;

import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.Visitor;

public class AstBinaryOpExpression implements AstExpression {
//	public enum Operator {Add, Substract, Mult, Divide}
	
	private BinaryOpExpression.Operator operator;
	
	private AstExpression left;
	private AstExpression right;
	
	private AstToken opToken;

	public AstBinaryOpExpression(AstExpression left, AstExpression right, AstToken opToken) {
		super();
//		this.operator = operator;
		this.left = left;
		this.right = right;
		this.opToken = opToken;
		
		String opText = opToken.getText();
		switch(opText) {
		case "+":
			this.operator = BinaryOpExpression.Operator.Add;
			break;
			
		case "-":
			this.operator = BinaryOpExpression.Operator.Substract;
			break;
			
		case "*":
			this.operator = BinaryOpExpression.Operator.Mult;
			break;
			
		case "/":
			this.operator = BinaryOpExpression.Operator.Divide;
			break;

		default:
			throw new RuntimeException("BinaryOpExpression: unknown operator " + opText);	// TODO: better error handling
		}
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

}
