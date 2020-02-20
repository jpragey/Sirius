package org.sirius.frontend.api.testimpl;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.StringType;

public class StringTypeTestImpl implements StringType {

	static QName qName = new QName("sirius", "lang", "String");
	
	@Override
	public QName getQName() {
		return qName;
	}

}
