package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.symbols.Scope;

import com.google.common.collect.ImmutableList;

public class FunctionDefinition implements Visitable {

	/** Partial, sorted by 
	 * For example, for f(x, y, z):
	 * 								closure
	 *  partials[0] = f()			x y z
	 *  partials[1] = f(x)			
	 *  partials[2] = f(x, y)
	 *  partials[3] = f(x, y, z)
	 *  */
	private List<Partial> partials;
	private Partial allArgsPartial;
	
//	private List<AstStatement> body;
	private FunctionBody body;
	
	
	private FunctionDeclaration functionDeclaration;
	
	private List<ClosureElement> closure;
	private Optional<FunctionDefinition> firstArgAppliedFuncDef;

	public FunctionDefinition(List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/, AstToken name, List<AstStatement> body) {
		this(List.of()/* closure*/, args, returnType, 
			member /* ie is an instance method*/,             
			name, 
			body);
	}

	public FunctionDefinition(List<ClosureElement> closure, List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/,             
			AstToken name, 
			List<AstStatement> body) 
	{
		super();
		this.closure = closure;
		this.partials = new ArrayList<>(args.size() + 1);
		this.body = new FunctionBody(body);
		
		this.functionDeclaration = new FunctionDeclaration(args, returnType, member, name); 
		
		int argSize = args.size();
		for(int from = 0; from <= argSize; from++) 
		{
			List<AstFunctionParameter> partialArgs = args.subList(0, from/*, argSize*/);
			
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
		this.firstArgAppliedFuncDef = applyOneArgToClosure(args, this.closure, this.functionDeclaration, body);
	}
	 
	private static Optional<FunctionDefinition> applyOneArgToClosure(List<AstFunctionParameter> currentArgs, List<ClosureElement> closure, FunctionDeclaration functionDeclaration,
			List<AstStatement> body) {
		
		if(currentArgs.isEmpty()) {
			return Optional.empty();
		}
		
		List<AstFunctionParameter> nextArgs = currentArgs.subList(1, currentArgs.size()); 
		
		List<ClosureElement> nextClosure = ImmutableList.<ClosureElement>builder()
				.addAll(closure)
				.add(new ClosureElement(currentArgs.get(0)))
				.build(); 
				
		FunctionDefinition applied = new FunctionDefinition(nextClosure, nextArgs, 
				functionDeclaration.getReturnType(), 
				functionDeclaration.isMember(), /* ie is an instance method*/             
				functionDeclaration.getName(), 
				body); 
		
		return Optional.of(applied);
	}
	
	public List<ClosureElement> getClosure() {
		return closure;
	}


	public void setContainerQName(QName containerQName) {
		this.functionDeclaration.setContainerQName(containerQName);
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
		this.functionDeclaration.visit(visitor);
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
		return functionDeclaration.getqName();
	}

	public AstToken getName() {
		return functionDeclaration.getName();
	}
	public String getNameString() {
		return getName().getText();
	}

	public FunctionBody getBody() {
		return body;
	}

	public FunctionDeclaration getFunctionDeclaration() {
		return functionDeclaration;
	}

	public Optional<FunctionDefinition> getFirstArgAppliedFunctionDef() {
		return firstArgAppliedFuncDef;
	}
	
	public List<AstFunctionParameter> getArgs() {
		return functionDeclaration.getArgs();
	}

	public AstType getReturnType() {
		return functionDeclaration.getReturnType();
	}

	/** Return the closed FunctionDefinition that has no argument (all args are in closure)  */
	public FunctionDefinition mostClosed() {
		return firstArgAppliedFuncDef.map(fd -> fd.mostClosed()).orElse(this);
	}
}
