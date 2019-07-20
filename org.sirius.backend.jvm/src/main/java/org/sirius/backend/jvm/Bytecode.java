package org.sirius.backend.jvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;

public class Bytecode {

	private byte[] bytes;

	public Bytecode(byte[] bytes, QName classQName) {
		super();
		this.bytes = bytes;
	}

	public Bytecode(ClassWriter classWriter, QName classQName) {
		this(classWriter.toByteArray(), classQName);
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public int size() {
		return bytes.length;
	}
	
	@Override
	public String toString() {
		return bytes.toString();
	}
	
	/** */
	public void createClassFiles(Reporter reporter, String classDir, QName classQName) {

		Path classFilePath = Paths.get(classDir, classQName.toArray());
		
		Path classDirPath = classFilePath.getParent();
		if(classDirPath == null) {
			reporter.error("Internal error: " + classFilePath.toString() + " has no parent.");
			return;
		} else {
			classDirPath.toFile().mkdirs();
		}
		
		
		File classFile = classDirPath.resolve(Paths.get(classFilePath.getFileName().toString() + ".class")).toFile();

		try(FileOutputStream writer = new FileOutputStream(classFile)) {
//			reporter.info("Writing bytecode to: " + classFile.getAbsolutePath());
			
			writer.write(bytes);
			
		} catch (FileNotFoundException e) {
			reporter.error("File not found: " + classFile.toString() + ": " + e.getMessage(), e);
		} catch (IOException e) {
			reporter.error("I/O error while writing " + classFile.toString() + ": " + e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
