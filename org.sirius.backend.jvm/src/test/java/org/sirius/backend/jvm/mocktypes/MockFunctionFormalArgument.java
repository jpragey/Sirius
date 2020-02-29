package org.sirius.backend.jvm.mocktypes;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Type;

public class MockFunctionFormalArgument implements FunctionFormalArgument {

	private QName qname;
	private Type type;
	
	public MockFunctionFormalArgument(QName qname, Type type) {
		super();
		this.qname = qname;
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public QName getQName() {
		return qname;
	}

}
