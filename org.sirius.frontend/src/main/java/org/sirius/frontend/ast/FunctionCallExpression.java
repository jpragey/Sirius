package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.Token;

public class FunctionCallExpression implements Expression {
	/** Function name */
	private AstToken name;
	
	private List<Expression> actualArguments = new ArrayList<>();

	public FunctionCallExpression(AstToken name) {
		super();
		this.name = name;
	}
	
	public FunctionCallExpression(Token name) {
		this(new AstToken(name));
	}

	public AstToken getName() {
		return name;
	}

	public List<Expression> getActualArguments() {
		return actualArguments;
	}
	
	public void addActualArgument(Expression argument) {
		this.actualArguments.add(argument);
	}

	@Override
	public Optional<Type> getType() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void visit(AstVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

