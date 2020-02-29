package org.sirius.frontend.api.testimpl;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ConstructorDeclaration;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Statement;

public class ConstructorDeclarationTestImpl implements ConstructorDeclaration {

	private QName qname;
	private List<FunctionFormalArgument> arguments;
	private List<Statement> bodyStatements;
	
	
	public ConstructorDeclarationTestImpl(QName qname, List<FunctionFormalArgument> arguments,
			List<Statement> bodyStatements) {
		super();
		this.qname = qname;
		this.arguments = arguments;
		this.bodyStatements = bodyStatements;
	}

	@Override
	public QName getQName() {
		return qname;
	}

	@Override
	public List<FunctionFormalArgument> getArguments() {
		return arguments;
	}

	@Override
	public List<Statement> getBodyStatements() {
		return bodyStatements;
	}

}
