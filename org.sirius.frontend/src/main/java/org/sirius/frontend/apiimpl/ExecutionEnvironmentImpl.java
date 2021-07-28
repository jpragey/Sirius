package org.sirius.frontend.apiimpl;

import java.util.List;

import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public class ExecutionEnvironmentImpl implements ExecutionEnvironment {

	private Type returnType;
	private List<Statement> bodyStatements;
	
	public ExecutionEnvironmentImpl(Type returnType, List<Statement> bodyStatements) {
		super();
		this.returnType = returnType;
		this.bodyStatements = bodyStatements;
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
