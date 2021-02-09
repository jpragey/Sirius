package org.sirius.frontend.ast;

import java.util.List;

public class LambdaDeclaration implements Verifiable {

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

	@Override
	public void verify(int featureFlags) {
		returnType.verify(featureFlags);
		verifyList(args, featureFlags);
	}
	
}
