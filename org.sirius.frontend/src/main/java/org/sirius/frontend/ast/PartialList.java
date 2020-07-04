package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;

public class PartialList implements Visitable {

	/** Partial, sorted by 
	 * For example, for f(x, y, z):
	 *  partials[0] = f()
	 *  partials[1] = f(x)
	 *  partials[2] = f(x, y)
	 *  partials[3] = f(x, y, z)
	 *  */
	private List<Partial> partials;
	private Partial allArgsPartial;
	
	private QName qName;
	private boolean concrete;
	private AstToken name;
	private List<AstStatement> statements;

	@Override
	public String toString() {
		return partials.stream()
				.map(part -> part.toString())
				.collect(Collectors.joining(", ", "{Part. " + name, "}"));
	}
	
	public PartialList(List<AstFunctionParameter> args /*List<Partial> partials*/, AstType returnType, 
			boolean member /* ie is an instance method*/,             
			QName qName, 
			boolean concrete /* ie has a body */, 
			AstToken name, List<AstStatement> statements) 
	{
		super();
		this.partials = new ArrayList<>(args.size() + 1);
		this.qName = qName;
		this.name = name;
		this.concrete = concrete;
		this.statements = statements;
		for(int from = 0; from <= args.size(); from++) 
		{
			List<AstFunctionParameter> partialArgs = args.subList(0, from); 
			Partial partial = new Partial(
					name,
					////				args.subList(0, from) .stream().map(arg -> new Capture(arg.getType(), arg.getName())).collect(Collectors.toList()), 
					partialArgs, 
					
					concrete,
//					function.isConcrete(), //boolean concrete,
					
					member,
//					function.isMember(),//boolean member,
					
					qName,
//					function.getQName(), //QName qName,
					
					returnType,
					statements
					);
			partials.add(partial);
			this.allArgsPartial = partial; // => last in partial list
		}
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
		visitor.startPartialList(this);
		for(Partial partial: partials) {
			partial.visit(visitor);
		}
		visitor.endPartialList(this);
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
		return name.getText();
	}

	public boolean isConcrete() {
		return concrete;
	}
	public List<AstStatement> getStatements() {
		return statements;
	}
	
}
