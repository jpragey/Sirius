package org.sirius.frontend.ast;

import java.util.List;

public class LambdaDefinition implements Verifiable, Visitable {

//	private LambdaDeclaration lambdaDeclaration;
	private AstType returnType; 
	private List<AstFunctionParameter> args;

	private FunctionBody body;
	

	public LambdaDefinition(List<AstFunctionParameter> args, AstType returnType, FunctionBody body) {
//		this.lambdaDeclaration = new LambdaDeclaration(args, returnType);
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

	public FunctionBody getBody() {
		return body;
	}

	@Override
	public void verify(int featureFlags) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startLambdaDefinition(this);
		visitor.endLambdaDefinition(this);
	}

	
}
