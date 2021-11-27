package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.sdk.SdkContent;

public interface IntegerType extends ClassType {

	default QName qName() {return SdkContent.siriusLangIntegerQName;}
	// TODO: check ???
	@Override
	default List<MemberValue> memberValues() {
		return List.of();
	}

	// TODO: check ???
	@Override
	default List<AbstractFunction> memberFunctions() {
		return List.of();
	}


}
