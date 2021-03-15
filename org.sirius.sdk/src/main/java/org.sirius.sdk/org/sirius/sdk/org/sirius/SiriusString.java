package org.sirius.sdk.org.sirius;

import org.sirius.sdk.tooling.Inherit;
import org.sirius.sdk.tooling.TopLevelClass;

@TopLevelClass(packageQName = "sirius.lang", name = "String")
@Inherit(packageQName = "sirius.lang", name = "Stringifiable")
public class SiriusString implements Addable<SiriusString>, Stringifiable {

	private String value;
	
	public SiriusString(String value) {
		this.value = value;
	}
	
	@Override
	public SiriusString string() {
		return this;
	}

	@Override
	public SiriusString add(SiriusString other) {
		return new SiriusString(this.value + other.value);
	}

}
