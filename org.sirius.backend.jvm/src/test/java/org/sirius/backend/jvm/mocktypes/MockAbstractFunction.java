package org.sirius.backend.jvm.mocktypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public class MockAbstractFunction implements AbstractFunction {

	private QName qname;
	private Type returnType;
	private List<FunctionFormalArgument> arguments;
	private List<Statement> bodyStatements;
	
	public MockAbstractFunction(QName qname, Type returnType) {
		super();
		this.qname = qname;
		this.returnType = returnType;
		this.arguments = new ArrayList<>();
		this.bodyStatements = new ArrayList<>();
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
	public Type getReturnType() {
		return returnType;
	}

	@Override
	public Optional<List<Statement>> getBodyStatements() {
		return Optional.of(bodyStatements);
	}

	@Override
	public Optional<QName> getClassOrInterfaceContainerQName() {
		return Optional.empty();
	}

}
