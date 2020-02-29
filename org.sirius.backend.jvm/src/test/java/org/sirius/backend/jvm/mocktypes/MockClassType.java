package org.sirius.backend.jvm.mocktypes;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassType;

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

}
