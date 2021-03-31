package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelClass;

@TopLevelClass(packageQName = "sirius.lang", name = "Function")
public interface SiriusFunction {
	@SiriusMethod
	SiriusInteger /*TODO*/ execute();
}
