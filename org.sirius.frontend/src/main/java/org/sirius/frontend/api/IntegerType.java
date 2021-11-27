package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.sdk.SdkContent;

public interface IntegerType extends ClassType {

	default QName getQName() {return SdkContent.siriusLangIntegerQName;}
	// TODO: check ???
	@Override
	default List<MemberValue> getMemberValues() {
		return List.of();
	}

	// TODO: check ???
	@Override
	default List<AbstractFunction> getFunctions() {
		return List.of();
	}


}
