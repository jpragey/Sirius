package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;

public interface FunctionClass {

	QName getQName();

	Type getReturnType();

	List<Statement> getBodyStatements();

}
