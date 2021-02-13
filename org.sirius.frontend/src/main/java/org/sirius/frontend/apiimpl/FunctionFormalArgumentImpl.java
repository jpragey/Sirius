package org.sirius.frontend.apiimpl;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Type;

public class FunctionFormalArgumentImpl implements FunctionFormalArgument {
	private QName argQName;
	private Type type;
	
	public FunctionFormalArgumentImpl(QName argQName, Type type) {
		super();
		this.argQName = argQName;
		this.type = type;
	}

	@Override
	public QName getQName() {
		return argQName;
	}

	@Override
	public Type getType() {
		return type;
	}
	@Override
	public String toString() {
		return "param. " + argQName.dotSeparated();
	}
}