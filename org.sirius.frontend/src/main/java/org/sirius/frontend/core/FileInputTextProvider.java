package org.sirius.frontend.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.sirius.common.error.Reporter;

public class FileInputTextProvider implements InputTextProvider {

	private Reporter reporter;
	private String packagePhysicalName;
	private String resourcePhysicalName;
	private String inputLocation;
	private Path filePath;
	private Charset charset;
	
	public FileInputTextProvider(Reporter reporter, File rootDirectory, String packagePhysicalName, String resourcePhysicalName, Charset charset) {
		super();
		this.reporter = reporter;
		this.packagePhysicalName = packagePhysicalName;
		this.resourcePhysicalName = resourcePhysicalName;
		this.inputLocation = packagePhysicalName + "/" + resourcePhysicalName;
		this.filePath = rootDirectory.
				toPath().
				resolve(packagePhysicalName).	// TODO: replace '/' by file separator? 
				resolve(resourcePhysicalName);
		this.charset = charset;
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
		try (FileInputStream is = new FileInputStream(filePath.toFile())) 
		{
			byte[] bytes = is.readAllBytes();
			String text = new String(bytes, charset); 
			return text;
		} catch (IOException e) {
			reporter.error("Can't read file " + filePath + ":" + e.getMessage(), e);
			return "";
		}
	}

}
