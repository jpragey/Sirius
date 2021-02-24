package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.Inherit;
import org.sirius.sdk.tooling.Inherits;
import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelClass;

@TopLevelClass(packageQName = "sirius.lang", name = "Float")
@Inherit(packageQName = "sirius.lang", name = "Addable")
@Inherit(packageQName = "sirius.lang", name = "Stringifiable")

public class SiriusFloat implements Addable<SiriusFloat>, Stringifiable
{
	private Float value;
	
	private SiriusFloat(float value) {
		this.value = value;
	}
	
	@SiriusMethod(methodName = "toString")
	public String asString() {
		return value.toString();
	}

	@SiriusMethod
	@Override
	public SiriusFloat add(SiriusFloat other) {
		return new SiriusFloat(value + other.value);
	}

	@Override
	public SiriusString string() {
		return new SiriusString(value.toString());
	}
}
