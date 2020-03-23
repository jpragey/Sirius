package org.sirius.frontend.api;

public interface VoidType extends Type {

	static VoidType instance = new VoidType() {
		@Override
		public boolean isAncestorOrSame(Type type) {
			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
		}
	};

}
