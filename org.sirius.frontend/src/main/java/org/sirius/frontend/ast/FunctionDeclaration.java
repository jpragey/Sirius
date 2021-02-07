package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.symbols.Scope;

public class FunctionDeclaration implements Visitable {

	
	private QName qName = null;
	private AstToken name;

	public void setContainerQName(QName containerQName) {
		this.qName = containerQName.child(new QName(name.getText()));
	}
	
	@Override
	public String toString() {
		return name.getText() + "(...)";
	}
	
	public FunctionDeclaration(List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/,             
			AstToken name) 
	{
		super();
		this.name = name;
		int argSize = args.size();
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startFunctionDeclaration(this);
		visitor.endFunctionDeclaration(this);
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

}
