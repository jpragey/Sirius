package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

public class LambdaDeclaration {

	private AstType returnType; 
	private List<AstFunctionParameter> args;
	private Optional<List<AstStatement>> body;

	public LambdaDeclaration(List<AstFunctionParameter> args, 
			AstType returnType, 
			Optional<List<AstStatement>> body) 
	{
		this.args = args;
		this.returnType = returnType;
		this.body = body;
		
	}

	public AstType getReturnType() {
		return returnType;
	}

	public List<AstFunctionParameter> getArgs() {
		return args;
	}

	public Optional<List<AstStatement>> getBody() {
		return body;
	}
	
	
}
