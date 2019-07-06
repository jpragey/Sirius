package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.TopLevelClass;

@TopLevelClass(packageQName = "sirius.lang", name = "Stringifiable")
public interface Stringifiable {

	public SiriusString string();
}
