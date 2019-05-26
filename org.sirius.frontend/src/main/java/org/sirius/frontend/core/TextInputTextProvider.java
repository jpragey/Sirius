package org.sirius.frontend.core;

public class TextInputTextProvider implements InputTextProvider {

	private String packagePhysicalName;
	private String resourcePhysicalName;
	private String inputLocation;
	private String content;
	
	public TextInputTextProvider(String packagePhysicalName, String resourcePhysicalName, String content) {
		super();
		this.packagePhysicalName = packagePhysicalName;
		this.resourcePhysicalName = resourcePhysicalName;
		this.inputLocation = packagePhysicalName + "/" + resourcePhysicalName;
		this.content = content;
	}

	@Override
	public String getPackagePhysicalName() {
		return packagePhysicalName;
	}

	@Override
	public String getResourcePhysicalName() {
		return resourcePhysicalName;
	}

	@Override
	public String getInputLocation() {
		return inputLocation;
	}

	@Override
	public String getText() {
		return content;
	}

	@Override
	public String toString() {
		return packagePhysicalName + "(" + inputLocation + ") \"" + content.substring(0, 60) + "...\"";
	}
}
