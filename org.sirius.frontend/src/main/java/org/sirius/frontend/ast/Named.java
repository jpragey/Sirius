package org.sirius.frontend.ast;

import org.sirius.common.core.QName;

public interface Named {

	AstToken getName();
	
	QName getQName();

}
