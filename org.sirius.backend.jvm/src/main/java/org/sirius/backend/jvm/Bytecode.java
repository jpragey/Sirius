package org.sirius.backend.jvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.objectweb.asm.ClassWriter;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;

public class Bytecode {

	private byte[] bytes;
	private QName classQName;

	public final static int VERSION = 55; // Java SE 11

	public Bytecode(byte[] bytes, QName classQName) {
		super();
		this.bytes = bytes;
		this.classQName = classQName;
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
	
	
	public QName getClassQName() {
		return classQName;
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
			writer.write(bytes);
			
		} catch (FileNotFoundException e) {
			reporter.error("File not found: " + classFile.toString() + ": " + e.getMessage(), e);
		} catch (IOException e) {
			reporter.error("I/O error while writing " + classFile.toString() + ": " + e.getMessage(), e);
		}
	}

}
