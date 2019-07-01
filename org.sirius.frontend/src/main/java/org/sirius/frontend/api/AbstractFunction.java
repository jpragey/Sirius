package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

public interface AbstractFunction {

	QName getQName();

	List<FunctionFormalArgument> getArguments();
	
	Type getReturnType();

	List<Statement> getBodyStatements();

}
