package org.sirius.frontend.api.testimpl;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.StringType;
import org.sirius.frontend.api.Type;

public class StringTypeTestImpl implements StringType {

	static QName qName = new QName("sirius", "lang", "String");
	
	@Override
	public QName getQName() {
		return qName;
	}
	
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}


}
