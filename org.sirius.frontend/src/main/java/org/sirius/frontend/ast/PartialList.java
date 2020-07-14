package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.symbols.Scope;

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
	
	private QName qName = null;
	private AstToken name;
	private Optional<List<AstStatement>> body;

	public void setContainerQName(QName containerQName) {
		this.qName = containerQName.child(new QName(name.getText()));
	}
	
	@Override
	public String toString() {
		return partials.stream()
				.map(part -> part.toString())
				.collect(Collectors.joining(", ", "{Part. " + name + ": ", "}"));
	}
	
	public PartialList(List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/,             
//			QName qName, 
			AstToken name, 
			Optional<List<AstStatement>> body) 
	{
		super();
		this.partials = new ArrayList<>(args.size() + 1);
//		this.qName = qName;
		this.name = name;
		this.body = body;
		int argSize = args.size();
		for(int from = 0; from <= argSize; from++) 
		{
			List<AstFunctionParameter> closure = args.subList(from, argSize);
			List<AstFunctionParameter> partialArgs = args.subList(0, from);
			
			Partial partial = new Partial(
					name,
					closure,
					partialArgs, 
					member,
					qName,
					returnType,
					body
					);
			partials.add(partial);
//			this.allArgsPartial = partial; // => last in partial list
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
		if(qName == null)
			throw new NullPointerException("qName for " + name.getText() + " - call setContainerQName() first");
			
		return qName;
	}

	public AstToken getName() {
		return name;
	}
	public String getNameString() {
		return name.getText();
	}

	public boolean isConcrete() {
		return body.isPresent();
	}
	public Optional<List<AstStatement>> getBody() {
		return body;
	}

	
}
