package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionClass;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public class FunctionClassImpl implements FunctionClass {

	private QName qname;
	private Type returnType;
	private List<Statement> bodyStatements;

	public FunctionClassImpl(QName qname, Type returnType, List<Statement> bodyStatements) {
		super();
		this.qname = qname;
		this.returnType = returnType;
		this.bodyStatements = bodyStatements;
	}

	@Override
	public QName getQName() {
		return qname;
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
