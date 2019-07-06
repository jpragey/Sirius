package org.sirius.frontend.ast;

import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.api.Expression;

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
	public Optional<AstType> getType() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void visit(AstVisitor visitor) {

		visitor.startBinaryOpExpression(this);
		
		left.visit(visitor);
		right.visit(visitor);
		
		visitor.endBinaryOpExpression(this);
	}

	@Override
	public Expression getExpression() {
		return new BinaryOpExpression() {

			@Override
			public Expression getLeft() {
				return left.getExpression();
			}

			@Override
			public Expression getRight() {
				return right.getExpression();
			}
		};
	}

}
