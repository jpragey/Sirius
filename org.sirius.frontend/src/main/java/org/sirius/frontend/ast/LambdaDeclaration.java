package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

import org.sirius.frontend.api.Type;

public class LambdaDeclaration implements AstType, Verifiable, Visitable {

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

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startLambdaDeclaration(this);
		visitor.endLambdaDeclaration(this);
	}

	
	
	@Override
	public String messageStr() {
	
		return "<lambda>()";
	}

	@Override
	public Type getApiType() {
		throw new UnsupportedOperationException("");	// TODO
	}

	@Override
	public AstType resolve() {
		throw new UnsupportedOperationException("");// TODO
	}

	@Override
	public boolean isExactlyA(AstType type) {
		throw new UnsupportedOperationException("");// TODO
	}

	@Override
	public boolean isStrictDescendantOf(AstType type) {
		throw new UnsupportedOperationException("");// TODO
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		throw new UnsupportedOperationException("");// TODO
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
