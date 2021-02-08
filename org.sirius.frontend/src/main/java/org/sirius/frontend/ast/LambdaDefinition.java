package org.sirius.frontend.ast;

import java.util.List;

public class LambdaDefinition {

	private LambdaDeclaration lambdaDeclaration;
	private FunctionBody body;
	

	public LambdaDefinition(List<AstFunctionParameter> args, AstType returnType, FunctionBody body) {
		this.lambdaDeclaration = new LambdaDeclaration(args, returnType);
		this.body = body;
	}

	public AstType getReturnType() {
		return lambdaDeclaration.getReturnType();
	}

	public List<AstFunctionParameter> getArgs() {
		return lambdaDeclaration.getArgs();
	}

	public FunctionBody getBody() {
		return body;
	}

	public LambdaDeclaration getLambdaDeclaration() {
		return lambdaDeclaration;
	}
	
}
