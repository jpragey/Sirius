package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

public class LambdaDeclaration implements Verifiable {

	private LambdaClosure closure;
	private AstType returnType; 
	private List<AstType> args;
	

	public LambdaDeclaration(LambdaClosure closure, List<AstType> args, AstType returnType) {
		this.closure = closure;
		this.args = args;
		this.returnType = returnType;
	}

	public LambdaDeclaration(List<AstType> args, AstType returnType) {
		this(new LambdaClosure(), args, returnType);
	}

	public AstType getReturnType() {
		return returnType;
	}

	public List<AstType> getArgTypes() {
		return args;
	}

	@Override
	public void verify(int featureFlags) {
		returnType.verify(featureFlags);
		verifyList(args, featureFlags);
	}

	/** Move the first functionparameter to end of closures, in a new LambdaDeclaration  
	 * 
	 * @return
	 */
//	public LambdaDeclaration applyParameter() {
//		assert(!args.isEmpty());
//		
//		AstType firstArg = args.get(0);
//		LambdaClosure newClosure = closure.appendEntry(firstArg, firstArg.getName(), Optional.empty() /*Init expression*/);
//		List<AstType> newArgs = args.subList(1, args.size()-1);
//		
//		LambdaDeclaration newLambda = new LambdaDeclaration(newClosure, newArgs, this.returnType);
//		return newLambda;
//	}

}
