package org.sirius.backend.jvm;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;

public class JarCreatorListener implements ClassWriterListener {

	private Reporter reporter;

	private static interface JvmOutputWriter {
		public void addToJar(Bytecode bytecode, QName classQName);
		public void writeClassFile(Bytecode bytecode, QName classDeclarationQName);
		public String getJarPathString();
		public void open(QName moduleQName);
		public void close();
	}
	private static class JvmFileOutputWriter implements JvmOutputWriter {
		private Reporter reporter;
		private Optional<JarOutputStream> jarOutputStream = Optional.empty();
		private Optional<String> classDir;
		private String modulePath;
		private String jarPathString = "<not set>";

		public JvmFileOutputWriter(Reporter reporter, String modulePath, Optional<String> classDir) {
			super();
			this.reporter = reporter;
			this.modulePath = modulePath;
			this.classDir = classDir;
		}
		@Override
		public void addToJar(Bytecode bytecode, QName classQName) {
			this.jarOutputStream.ifPresent(jarOS -> {
				ZipEntry ze = new ZipEntry(classQName.slashSeparated() + ".class");
				try {
					jarOS.putNextEntry(ze);
					jarOS.write(bytecode.getBytes());
					jarOS.closeEntry();
				} catch (IOException e) {
					reporter.error("Can't write entry " + classQName + " to jar file " + jarPathString /* jarPath.toAbsolutePath()*/
						+ ": " + e.getMessage(), e);
				}
			});
		}
		@Override
		public void writeClassFile(Bytecode bytecode, QName classDeclarationQName ) {
			classDir.ifPresent(cdir -> bytecode.createClassFiles(reporter, cdir, classDeclarationQName));
		}
		@Override
		public String getJarPathString() {
			return jarPathString;
		}
		@Override
		public void open(QName moduleQName) {
			QName effectiveQName = moduleQName.isEmpty() ?
					new QName("unnamed.jar") :
					moduleQName.parent().get().child(moduleQName.getLast() + ".jar");
				
			Path jarPath = Paths.get(modulePath, effectiveQName.toArray());
			
			File jarFile = jarPath.toFile();
			this.jarPathString = jarPath.toAbsolutePath().toString();

			QName manifestMainClassQName = moduleQName.child(Util.jvmPackageClassName);
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
				attributes.put(Attributes.Name.MAIN_CLASS, manifestMainClassQName.dotSeparated() /* "$package$"*/);

				JarOutputStream os = new JarOutputStream(out, manifest);
				this.jarOutputStream = Optional.of(os);
				
			} catch (IOException e) {
				reporter.error("Can't open to jar file " + jarFile.getAbsolutePath(), e);
			}
			
		}
		@Override
		public void close() {
			jarOutputStream.ifPresent(os -> {
				try {
					os.close();
				} catch (IOException e) {
					reporter.error("Can't close JAR file for module " + jarPathString, e);
				}	
			});
		}

		
	}
	public static class JvmByteOutputStreamWriter implements JvmOutputWriter {
		private Reporter reporter;
		private HashMap<QName /*class QName */, Bytecode> bytecodeMap;

		public JvmByteOutputStreamWriter(Reporter reporter, HashMap<QName /*class QName */, Bytecode> bytecodeMap) {
			super();
			this.reporter = reporter;
			this.bytecodeMap = bytecodeMap;
		}

		@Override
		public void writeClassFile(Bytecode bytecode, QName classDeclarationQName) {
			bytecodeMap.put(classDeclarationQName, bytecode);
		}

		@Override
		public String getJarPathString() {
			return "<JAR path not set>";
		}

		@Override
		public void open(QName moduleQName) {
		}

		@Override
		public void close() {
		}

		@Override
		public void addToJar(Bytecode bytecode, QName classQName) {
			// Nothing to do
		}
	}
	
	private JvmOutputWriter outputWriter;
	
	private JarCreatorListener(Reporter reporter, JvmOutputWriter outputWriter) {
		this.reporter = reporter;
		this.outputWriter = outputWriter;
	}

	/**
	 * 
	 * @param reporter
	 * @param modulePath   module directory path, as given by '--module' option
	 * @param moduleQName
	 */
	public static JarCreatorListener createAsFile(Reporter reporter, String modulePath, Optional<String> classDir) {
		JvmOutputWriter ow = new JvmFileOutputWriter(reporter, modulePath, classDir);
		return new JarCreatorListener(reporter, ow);
	}
	public static JarCreatorListener createInMemoryMap(Reporter reporter, HashMap<QName /*class QName */, Bytecode> bytecodeMap) {
		JvmOutputWriter ow = new JvmByteOutputStreamWriter(reporter, bytecodeMap);
		return new JarCreatorListener(reporter, ow);
	}
	
	@Override
	public void start(ModuleDeclaration moduleDeclaration) {
//		System.out.println(" ++ Start module creation in " + modulePath + " for module '" + moduleQName + "'");
		QName moduleQName = moduleDeclaration.getQName();
		outputWriter.open(moduleQName);
	}

	@Override
	public void addByteCode(Bytecode bytecode, QName classQName) {
		outputWriter.addToJar(bytecode, classQName);
		outputWriter.writeClassFile(bytecode, classQName);
	}
	
	@Override
	public void end() {
		outputWriter.close();
	}

}
