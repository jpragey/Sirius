package org.sirius.backend.jvm.mocktypes;

import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.VoidType;

public class MockVoidType implements VoidType {
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}

}
