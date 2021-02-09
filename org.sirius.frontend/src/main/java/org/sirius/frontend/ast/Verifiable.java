package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

/**   */
public interface Verifiable {

	static final int APICacheFlag = 0x01;	// check cached API objects exists in AST objects
	
	/**
	 * Basic features are always verified. Other features depend on #featureFlags.
	 * @param featureFlags bitfield, can be APICacheFlag
	 */
	public void verify(int featureFlags);	// throws some assertion if object state is not valid

//	public default verifyAPICachedNoNull(Object cached, )
	public default void verifyNotNull(Object obj, String message) {
		if(obj == null)
			throw new AssertionError("Unexpected null checked in Verifiable, " + message + ": in " + toString());
	}

	public default void verifyCachedObjectNotNull(Object obj, String message, int featureFlags) {
		if((featureFlags & APICacheFlag) != 0)
			verifyNotNull(obj, "null cached object: " + message);
	}

	public default void optionalIsPresent(Optional<?> opt, String message) {
		if(!opt.isPresent()) {
			throw new AssertionError("Optional<> not present, " + message + ": in " + toString());
		}
	}
	public default void verifyOptional(Optional<? extends Verifiable> opt, String message, int featureFlags) {
		optionalIsPresent(opt, message);
		opt.get().verify(featureFlags);
	}
	
	public default <T extends Verifiable> void verifyList(List<T> verifiables, int featureFlags) {
		for(Verifiable v: verifiables) {
			v.verify(featureFlags);
		}
	}
	
}
