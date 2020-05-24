package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.Inherit;
import org.sirius.sdk.tooling.Inherits;
import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelClass;

@TopLevelClass(packageQName = "sirius.lang", name = "Boolean")
//@Inherit(packageQName = "sirius.lang", name = "Addable")
@Inherit(packageQName = "sirius.lang", name = "Stringifiable")

public class SiriusBoolean implements /*Addable<SiriusBoolean>, */Stringifiable
{
	private Boolean value;
	
	private SiriusBoolean(boolean value) {
		this.value = value;
	}
	
	@SiriusMethod(methodName = "toString")
	public String asString() {
		return value.toString();
	}

//	@SiriusMethod
//	@Override
//	public SiriusBoolean add(SiriusBoolean other) {
//		return new SiriusBoolean(value + other.value);
//	}

	@Override
	public SiriusString string() {
		return new SiriusString(value.toString());
	}
}
