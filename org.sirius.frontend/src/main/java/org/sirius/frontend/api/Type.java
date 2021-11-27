package org.sirius.frontend.api;

import java.util.Optional;

public interface Type {

	public static IntegerType integerType = new IntegerType() {
//		@Override
//		public boolean isAncestorOrSame(Type type) {
//			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
//		}
		@Override
		public String toString() {
			return "Type.integerType";
		}

		@Override
		public Optional<ExecutionEnvironment> getExecutionEnvironment() {
			return Optional.empty();
		}
	};
	public static FloatType floatType = new FloatType() {
//		@Override
//		public boolean isAncestorOrSame(Type type) {
//			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
//		}
		@Override
		public String toString() {
			return "Type.floatType";
		}
	};
	public static BooleanType booleanType = new BooleanType() {
//		@Override
//		public boolean isAncestorOrSame(Type type) {
//			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
//		}
		@Override
		public String toString() {
			return "Type.booleanType";
		}
		
	};
	public static VoidType voidType = new VoidType() {
		@Override
		public String toString() {
			return "Type.voidType";
		}
	};
	
//	default boolean isAncestorOrSame(Type type) {
//		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
//	}
	
}
