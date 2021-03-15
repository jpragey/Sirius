package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelClass;

@TopLevelClass(packageQName = "sirius.lang", name = "Addable")
public interface Addable<T> {

	@SiriusMethod
	public T add(T other);
}
