package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.symbols.Scope;

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
	
	private Optional<List<AstStatement>> body;

	private FunctionDeclaration functionDeclaration;
	
	public void setContainerQName(QName containerQName) {
		this.functionDeclaration.setContainerQName(containerQName);
	}
	
	@Override
	public String toString() {
		return partials.stream()
				.map(part -> part.toString())
				.collect(Collectors.joining(", ", "{Part. " + getName() + ": ", "}"));
	}
	
	public FunctionDefinition(List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/,             
			AstToken name, 
			Optional<List<AstStatement>> body) 
	{
		super();
		this.partials = new ArrayList<>(args.size() + 1);
		this.body = body;
		
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

	public boolean isConcrete() {
		return body.isPresent();
	}
	public Optional<List<AstStatement>> getBody() {
		return body;
	}

	public FunctionDeclaration getFunctionDeclaration() {
		return functionDeclaration;
	}
	
}
