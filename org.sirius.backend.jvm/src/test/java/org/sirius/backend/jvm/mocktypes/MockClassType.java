package org.sirius.backend.jvm.mocktypes;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;

public class MockClassType implements ClassType {

	private QName qname;
	
	public MockClassType(QName qname) {
		super();
		this.qname = qname;
	}

	@Override
	public QName getQName() {
		return qname;
	}
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}

	@Override
	public List<MemberValue> getMemberValues() {
		return List.of();
	}

	@Override
	public List<AbstractFunction> getFunctions() {
		return List.of();
	}

}
