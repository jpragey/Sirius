package org.sirius.frontend.api;

import java.util.List;

public interface FunctionDeclaration extends Type {

	Type getReturnType();

	List<Type> getParameterTypes();

}
