package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.symbols.Scope;

public class FunctionDeclaration implements Visitable, Verifiable {

	
	private QName qName = null;
	private AstToken name;
	
	private LambdaDeclaration lambdaDeclaration;
	private List<AstFunctionParameter> args; // TODO: remove redundancy args / lambdaDeclaration  
	
	private boolean member /* ie is an instance method*/;
	
	
	public FunctionDeclaration(List<AstFunctionParameter> args, AstType returnType, 
			boolean member /* ie is an instance method*/,             
			AstToken name) 
	{
		super();
		this.member = member;
		this.name = name;
		this.args = args;
		
		this.lambdaDeclaration = new LambdaDeclaration(args.stream().map(p -> p.getType()).collect(Collectors.toUnmodifiableList()), returnType);
	}
	public void setContainerQName(QName containerQName) {
		this.qName = containerQName.child(new QName(name.getText()));
	}
	
	@Override
	public String toString() {
		return name.getText() + "(...)";
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

	public List<AstFunctionParameter> getArgs() {
		return args;
	}

	public boolean isMember() {
		return member;
	}

	public AstType getReturnType() {
		return lambdaDeclaration.getReturnType();
	}
	@Override
	public void verify(int featureFlags) {
		verifyNotNull(qName, "qName");
		
		lambdaDeclaration.verify(featureFlags);
	}

}
