package org.sirius.backend.jvm.mocktypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public class MockAbstractFunction implements AbstractFunction {

	private QName qname;
	private Type returnType;
	private List<FunctionParameter> arguments;
	private List<Statement> bodyStatements;
	
	public MockAbstractFunction(QName qname, Type returnType) {
		super();
		this.qname = qname;
		this.returnType = returnType;
		this.arguments = new ArrayList<>();
		this.bodyStatements = new ArrayList<>();
	}

	@Override
	public QName qName() {
		return qname;
	}


	@Override
	public List<FunctionParameter> parameters() {
		return arguments;
	}

	@Override
	public Type returnType() {
		return returnType;
	}

	@Override
	public Optional<List<Statement>> bodyStatements() {
		return Optional.of(bodyStatements);
	}

	@Override
	public Optional<QName> getClassOrInterfaceContainerQName() {
		return Optional.empty();
	}

}
