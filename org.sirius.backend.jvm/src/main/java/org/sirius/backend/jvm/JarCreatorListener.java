package org.sirius.backend.jvm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;

public class JarCreatorListener implements ClassWriterListener {

	private Reporter reporter;
	private String modulePath;
	private Optional<String> classDir;
//	private QName moduleQName;
	private Optional<JarOutputStream> outputStream = Optional.empty();
	private Path jarPath;
	
	
	/**
	 * 
	 * @param reporter
	 * @param modulePath   module directory path, as given by '--module' option
	 * @param moduleQName
	 */
	public JarCreatorListener(Reporter reporter, String modulePath/*, QName moduleQName*/, Optional<String> classDir) {
		super();
		this.reporter = reporter;
		this.modulePath = modulePath;
		this.classDir = classDir;
//		this.moduleQName = moduleQName;

//		QName effectiveQName = moduleQName.isEmpty() ?
//			new QName("unnamed.jar") :
//			moduleQName.parent().get().child(moduleQName.getLast() + ".jar");
//		
//		this.jarPath = Paths.get(modulePath, effectiveQName.toArray());
	}

	@Override
	public void start(ModuleDeclaration moduleDeclaration) {
//		System.out.println(" ++ Start module creation in " + modulePath + " for module '" + moduleQName + "'");
		QName moduleQName = moduleDeclaration.getQName();
		QName effectiveQName = moduleQName.isEmpty() ?
				new QName("unnamed.jar") :
				moduleQName.parent().get().child(moduleQName.getLast() + ".jar");
			
		this.jarPath = Paths.get(modulePath, effectiveQName.toArray());
		
		File jarFile = jarPath.toFile();
		try {
			File parentDir = jarFile.getParentFile();
			if(parentDir!= null) {
				parentDir.mkdirs();
			}
			if(jarFile.isFile()) {
				jarFile.delete();
			}
			OutputStream out = new BufferedOutputStream(new FileOutputStream(jarFile));
			
			Manifest manifest = new Manifest();
			Attributes attributes = manifest.getMainAttributes();
			attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
			attributes.put(Attributes.Name.MAIN_CLASS, "$package$");
			
			this.outputStream = Optional.of(new JarOutputStream(out, manifest));
		} catch (Exception e) {
			reporter.error("Can't open to jar file " + jarFile.getAbsolutePath(), e);
		}

		
	}

	@Override
	public void addByteCode(Bytecode bytecode, QName classQName) {
//		System.out.println(" ++ Adding bytecode to module " + modulePath + " : " + bytecode.size() + " bytes, to class: " + qName);
		
		addToJar(bytecode, classQName);
		writeClassFile(bytecode, classQName);
	}
	
	private void addToJar(Bytecode bytecode, QName classQName) {
		this.outputStream.ifPresent(jarOS -> {
			ZipEntry ze = new ZipEntry(classQName.dotSeparated() + ".class");
			try {
				jarOS.putNextEntry(ze);
				jarOS.write(bytecode.getBytes());
				jarOS.closeEntry();
			} catch (IOException e) {
				reporter.error("Can't write entry " + classQName + " to jar file " + jarPath.toAbsolutePath()+ ": " + e.getMessage(), e);
			}
		});
	}
	
	private void writeClassFile(Bytecode bytecode, QName classDeclarationQName ) {
		classDir.ifPresent(cdir -> bytecode.createClassFiles(reporter, cdir, classDeclarationQName));
	}
	

	@Override
	public void end() {
//		System.out.println(" ++ End module creation in " + modulePath);
		outputStream.ifPresent(os -> {
			try {
				os.close();
			} catch (IOException e) {
				reporter.error("Can't close to jar file " + jarPath.toAbsolutePath(), e);
			}
		});

	}

}
