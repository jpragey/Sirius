package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionCall;

public class AstFunctionCallExpression implements AstExpression {
	/** Function name */
	private AstToken name;
	
	private List<AstExpression> actualArguments = new ArrayList<>();

	public AstFunctionCallExpression(AstToken name) {
		super();
		this.name = name;
	}
	
	public AstFunctionCallExpression(Token name) {
		this(new AstToken(name));
	}

	public AstToken getName() {
		return name;
	}

	public List<AstExpression> getActualArguments() {
		return actualArguments;
	}
	
	public void addActualArgument(AstExpression argument) {
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

	@Override
	public Expression getExpression() {
		return new FunctionCall() {
			
			@Override
			public org.sirius.common.core.Token getFunctionName() {
				return name.asToken();
			}
		};
	}
	
	
	
}

