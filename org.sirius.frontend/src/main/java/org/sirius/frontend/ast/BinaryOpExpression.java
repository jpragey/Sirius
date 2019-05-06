package org.sirius.frontend.ast;

import java.util.Optional;

import org.antlr.v4.runtime.Token;

public class BinaryOpExpression implements Expression {
	public enum Operator {Add, Substract, Mult, Divide}
	
	private Operator operator;
	
	private Expression left;
	private Expression right;
	
	private AstToken opToken;

	public BinaryOpExpression(Expression left, Expression right, AstToken opToken) {
		super();
//		this.operator = operator;
		this.left = left;
		this.right = right;
		this.opToken = opToken;
		
		String opText = opToken.getText();
		switch(opText) {
		case "+":
			this.operator = Operator.Add;
			break;
			
		case "-":
			this.operator = Operator.Substract;
			break;
			
		case "*":
			this.operator = Operator.Mult;
			break;
			
		case "/":
			this.operator = Operator.Divide;
			break;

		default:
			throw new RuntimeException("BinaryOpExpression: unknown operator " + opText);	// TODO: better error handling
		}
	}

	public BinaryOpExpression(Expression left, Expression right, Token opToken) {
		this(left, right, new AstToken(opToken));
	}
		
	@Override
	public Optional<Type> getType() {
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

}
