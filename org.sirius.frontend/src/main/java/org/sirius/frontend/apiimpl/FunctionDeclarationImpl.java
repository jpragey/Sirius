package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.frontend.api.FunctionDeclaration;
import org.sirius.frontend.api.Type;

public class FunctionDeclarationImpl implements FunctionDeclaration {
	
	private Type returnType;
	private List<Type> parameterTypes;
	
	public FunctionDeclarationImpl(Type returnType, List<Type> parameterTypes) {
		super();
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public Type getReturnType() {
		return returnType;
	}

	@Override
	public List<Type> getParameterTypes() {
		return parameterTypes;
	}

	@Override
	public String toString() {
		return returnType.toString() + "(" + parameterTypes.stream().map(Object::toString).collect(Collectors.joining(",", "(", ")"));
	}
}
