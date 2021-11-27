package org.sirius.frontend.api;

import java.util.List;

public interface FunctionDeclaration extends Type {

	Type returnType();

	List<Type> parameterTypes();

}
