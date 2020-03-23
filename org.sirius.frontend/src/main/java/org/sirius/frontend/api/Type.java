package org.sirius.frontend.api;

public interface Type {

	public static IntegerType integerType = new IntegerType() {
		@Override
		public boolean isAncestorOrSame(Type type) {
			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
		}
		
	};
	public static FloatType floatType = new FloatType() {
		@Override
		public boolean isAncestorOrSame(Type type) {
			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
		}
		
	};
	public static BooleanType booleanType = new BooleanType() {
		@Override
		public boolean isAncestorOrSame(Type type) {
			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
		}
		
	};
	public static VoidType voidType = new VoidType() {
		@Override
		public boolean isAncestorOrSame(Type type) {
			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
		}
	};
	
	boolean isAncestorOrSame(Type type);
	
}
