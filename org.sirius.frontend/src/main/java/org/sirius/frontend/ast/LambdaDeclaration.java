package org.sirius.frontend.ast;

import java.util.List;

public class LambdaDeclaration {

	private AstType returnType; 
	private List<AstFunctionParameter> args;
	

	public LambdaDeclaration(List<AstFunctionParameter> args, AstType returnType) {
		this.args = args;
		this.returnType = returnType;
	}

	public AstType getReturnType() {
		return returnType;
	}

	public List<AstFunctionParameter> getArgs() {
		return args;
	}
	
}
