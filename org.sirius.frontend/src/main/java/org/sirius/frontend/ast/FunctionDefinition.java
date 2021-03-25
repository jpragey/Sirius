package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;

public class FunctionDefinition implements Visitable, Verifiable {

	/** Partial, sorted by 
	 * For example, for f(x, y, z):
	 * 								closure
	 *  partials[0] = f()			x y z
	 *  partials[1] = f(x)			
	 *  partials[2] = f(x, y)
	 *  partials[3] = f(x, y, z)
	 *  */
	private AstToken name;
	private QName qName;
	private List<Partial> partials;
	private Partial allArgsPartial;
	
	private LambdaDefinition lambdaDefinition;
	
	private LambdaClosure closure;
	private Optional<FunctionDefinition> firstArgAppliedFuncDef;

	private boolean member;
	
	public FunctionDefinition(List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/, AstToken name, List<AstStatement> body) {
		this(new LambdaClosure(), args, returnType, 
			member /* ie is an instance method*/,             
			name, 
			body);
	}

	public FunctionDefinition(LambdaClosure closure, List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/,             
			AstToken name, 
			List<AstStatement> body) 
	{
		super();
		this.member = member;
		this.closure = closure;
		this.partials = new ArrayList<>(args.size() + 1);
		this.lambdaDefinition = new LambdaDefinition(args, returnType, new FunctionBody(body));
		this.name = name;
		
		int argSize = args.size();
		for(int from = 0; from <= argSize; from++) 
		{
			List<AstFunctionParameter> partialArgs = args.subList(0, from);
			
			Partial partial = new Partial(
					name,
					partialArgs, 
					member,
					returnType,
					body
					);
			partials.add(partial);
		}
		
		this.allArgsPartial = partials.get(argSize); // => last in partial list
		
		this.firstArgAppliedFuncDef = applyOneArgToClosure(args, this.closure, body);
	}
	 
	private Optional<FunctionDefinition> applyOneArgToClosure(List<AstFunctionParameter> currentArgs, LambdaClosure /* List<ClosureElement>*/ closure, //FunctionDeclaration functionDeclaration,
			List<AstStatement> body) {
		
		if(currentArgs.isEmpty()) {
			return Optional.empty();
		}
		
		List<AstFunctionParameter> nextArgs = currentArgs.subList(1, currentArgs.size()); 
		
		LambdaClosure nextClosure = closure.appendEntry(new ClosureElement(currentArgs.get(0)));
				
		FunctionDefinition applied = new FunctionDefinition(nextClosure, nextArgs, 
				lambdaDefinition.getReturnType(),
				member,
				name,
				body); 
		
		return Optional.of(applied);
	}
	
	public LambdaClosure getClosure() {
		return closure;
	}


	public void setContainerQName(QName containerQName) {
		this.qName = containerQName.child(name.getText());
	}
	
	@Override
	public String toString() {
		return partials.stream()
				.map(part -> part.toString())
				.collect(Collectors.joining(", ", "{Part. " + getName() + ": ", "}"));
	}

	public List<Partial> getPartials() {
		return partials;
	}

	/** Get the partial with all args*/
	public Partial getAllArgsPartial() {
		return allArgsPartial;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startFunctionDefinition(this);
		this.lambdaDefinition.visit(visitor);
		for(Partial partial: partials) {
			partial.visit(visitor);
		}
		visitor.endFunctionDefinition(this);
	}

	public Optional<Partial> byArgCount(int argCount) {
		if(argCount <partials.size()) {
			Partial p = partials.get(argCount);
			assert(p.getArgs().size() == argCount);
			return Optional.of(p);
		}
		return Optional.empty();
	}
	
	
	public QName getqName() {
		return qName;
	}

	public AstToken getName() {
		return name;
	}
	public String getNameString() {
		return getName().getText();
	}

	public FunctionBody getBody() {
		return lambdaDefinition.getBody();
	}

	public Optional<FunctionDefinition> getFirstArgAppliedFunctionDef() {
		return firstArgAppliedFuncDef;
	}
	
	public List<AstFunctionParameter> getArgs() {
		return lambdaDefinition.getArgs();
	}

	public AstType getReturnType() {
		return lambdaDefinition.getReturnType();
	}

	/** Return the closed FunctionDefinition that has no argument (all args are in closure)  */
	public FunctionDefinition mostClosed() {
		return firstArgAppliedFuncDef.map(fd -> fd.mostClosed()).orElse(this);
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(partials, featureFlags);
		allArgsPartial.verify(featureFlags);
		
//		body.verify(featureFlags);
		
		verifyNotNull(qName, "qName");
		lambdaDefinition.verify(featureFlags);
		
//		verifyList(closure, featureFlags);
		verifyOptional(firstArgAppliedFuncDef, "firstArgAppliedFuncDef", featureFlags);
	}
}
