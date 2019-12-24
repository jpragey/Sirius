package org.sirius.frontend.api.testimpl;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.VoidType;

public class MemberFunctionTestImpl implements MemberFunction {

	private QName qname;

	public ArrayList<FunctionFormalArgument> formalArguments = new ArrayList<>();
	public Type returnType = new VoidType() {}; // TODO: ???
	public ArrayList<Statement> bodyStatements = new ArrayList<>();

	public MemberFunctionTestImpl(QName qname, List<FunctionFormalArgument> formalArguments, Type returnType,
			List<Statement> bodyStatements) {
		super();
		this.qname = qname;
		this.formalArguments = new ArrayList<FunctionFormalArgument>(formalArguments);
		this.returnType = returnType;
		this.bodyStatements = new ArrayList<Statement>(bodyStatements);
	}

	public MemberFunctionTestImpl(QName qname) {
		super();
		this.qname = qname;
	}

	public MemberFunctionTestImpl addArgument(FunctionFormalArgument argument) {
		this.formalArguments.add(argument);
		return this;
	}
	
	public MemberFunctionTestImpl setReturnType(Type returnType) {
		this.returnType = returnType;
		return this;
	}
	
	public MemberFunctionTestImpl addBodyStatement(Statement statement) {
		this.bodyStatements.add(statement);
		return this;
	}
	
	
	@Override
	public QName getQName() {
		return qname;
	}

	@Override
	public List<FunctionFormalArgument> getArguments() {
		return formalArguments;
	}

	@Override
	public Type getReturnType() {
		return returnType;
	}

	@Override
	public List<Statement> getBodyStatements() {
		return bodyStatements;
	}

}
