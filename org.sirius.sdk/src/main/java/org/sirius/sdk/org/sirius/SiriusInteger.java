package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.Inherit;
import org.sirius.sdk.tooling.Inherits;
import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelClass;

@TopLevelClass(packageQName = "sirius.lang", name = "Integer")
@Inherit(packageQName = "sirius.lang", name = "Addable")
@Inherit(packageQName = "sirius.lang", name = "Stringifiable")

public class SiriusInteger implements Addable<SiriusInteger>, Stringifiable
{
	private Integer value;
	
	private SiriusInteger(int value) {
		this.value = value;
	}
	
	@SiriusMethod(methodName = "toString")
	public String asString() {
		return value.toString();
	}

	@SiriusMethod
	@Override
	public SiriusInteger add(SiriusInteger other) {
		return new SiriusInteger(value + other.value);
	}

	@Override
	public SiriusString string() {
		return new SiriusString(value.toString());
	}
}
