package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

public interface ExecutionEnvironment {
	Type getReturnType();
	List<Statement> getBodyStatements();

}
