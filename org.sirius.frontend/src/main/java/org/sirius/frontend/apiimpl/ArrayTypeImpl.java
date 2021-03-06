package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.Type;

public class ArrayTypeImpl implements ArrayType {
	private Type apiType;
	
	public ArrayTypeImpl(Type apiType) {
		super();
		this.apiType = apiType;
	}
	@Override
	public Type getElementType() {
		return apiType;
	}
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}
}
