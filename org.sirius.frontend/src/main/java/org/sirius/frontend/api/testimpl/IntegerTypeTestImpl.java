package org.sirius.frontend.api.testimpl;

import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.Type;

public class IntegerTypeTestImpl implements IntegerType {
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}

}
