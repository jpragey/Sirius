package org.sirius.frontend.apiimpl;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.Scope;
import org.sirius.frontend.api.Type;

public record FunctionFormalArgumentImpl(QName qName, Type type) implements FunctionParameter {

	@Override
	public String toString() {
		return "param. " + qName.dotSeparated();
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public QName getQName() {
		return qName;
	}
}