package org.sirius.frontend.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.sirius.common.error.Reporter;

public class ResourceInputTextProvider implements InputTextProvider {

	private Reporter reporter;
	private String root;
	private String packagePhysicalName;
	private String resourcePhysicalName;
	private String inputLocation;

	
	public ResourceInputTextProvider(Reporter reporter, String root, String packagePhysicalName, String resourcePhysicalName) {
		super();
		this.reporter = reporter;
		this.root = root;
		this.packagePhysicalName = packagePhysicalName;
		this.resourcePhysicalName = resourcePhysicalName;
		this.inputLocation = packagePhysicalName + "/" + resourcePhysicalName;
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
		String path = this.root + "/" + this.packagePhysicalName + "/" + this.resourcePhysicalName;
		InputStream is = getClass().getResourceAsStream(path);
		if(is == null) {
			reporter.error("Resource '" + path + "' not found (or couldn't be read)");
			return "";
		}
		try {
			Charset charset = Charset.forName("UTF-8"); // TODO: use cli-like parameters + check runtime exceptions
			
			byte [] bytes = is.readAllBytes();
			String sourceText = new String(bytes, charset);
			return sourceText;
		} catch (IOException e) {
			reporter.error("Can't read resource " + path + ":" + e.getMessage(), e);
			return "";
		}
	}



}
