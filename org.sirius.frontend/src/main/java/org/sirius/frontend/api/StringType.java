package org.sirius.frontend.api;

import org.sirius.common.core.QName;
import org.sirius.frontend.sdk.SdkContent;

public interface StringType extends ClassType {

	default QName getQName() {return SdkContent.siriusLangStringQName;}

}
