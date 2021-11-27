package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;

public interface FunctionClass {

	QName qName();

	Type returnType();

	List<Statement> bodyStatements();

}
