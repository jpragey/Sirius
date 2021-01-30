package org.sirius.frontend.core.stdlayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.sirius.frontend.core.InputTextProvider;

public class StdInputTextProvider implements InputTextProvider {
	private File file;
	private String text;
	private String inputLocation;
	private String packagePhysicalName;
	private String resourcePhysicalName;
	
	public StdInputTextProvider(File file, String inputLocation, String packagePhysicalName, String resourcePhysicalName,
			String text) {
		super();
		this.file = file;
		this.inputLocation = inputLocation;
		this.packagePhysicalName = packagePhysicalName;
		this.resourcePhysicalName = resourcePhysicalName;
		this.text = text;
	}

	public static StdInputTextProvider sourceFile(File file, List<String> pkgQNameElements) throws IOException {
		try(BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8 /*TODO*/)) {
			StringBuilder sb = new StringBuilder((int)file.length());
			reader.lines().forEach(line -> sb.append(line));
			String text = sb.toString();
			
			String packagePhysicalName = String.join("/", pkgQNameElements);	// '/' separated package qname
			String resourcePhysicalName = file.getName();	// , // File simple name eg "Object.sirius
			
			return new StdInputTextProvider(file, file.getAbsolutePath(), packagePhysicalName, resourcePhysicalName, text);
		}
	}
	
	public File getFile() {
		return file;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getInputLocation() {
		return inputLocation;
	}

	@Override
	public String getPackagePhysicalName() {
		return packagePhysicalName;
	}

	@Override
	public String getResourcePhysicalName() {
		return resourcePhysicalName;
	}
}