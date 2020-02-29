package org.sirius.frontend.api.testimpl;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassType;

public class ClassTypeImpl implements ClassType {

	private QName qname;
	
	public ClassTypeImpl(QName qname) {
		super();
		this.qname = qname;
	}

	@Override
	public QName getQName() {
		return qname;
	}

}
