package org.sirius.frontend.api.testimpl;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Type;

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
	
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}


}
