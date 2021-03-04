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
//	private Optional<String> classDir;
//	private Path jarPath;
	

	private static interface JvmOutputWriter {
		public void addToJar(Bytecode bytecode, QName classQName);
//		public Optional<OutputStream> getOutputStream();
		public void writeClassFile(Bytecode bytecode, QName classDeclarationQName);
		public String getJarPathString();
		public void open(QName moduleQName);
		public void close();
	}
	private static class JvmFileOutputWriter implements JvmOutputWriter {
		private Reporter reporter;
//		private Optional<OutputStream> outputStream = Optional.empty();
		private Optional<JarOutputStream> jarOutputStream = Optional.empty();
		private Optional<String> classDir;
		private String modulePath;
		private String jarPathString = "<not set>";
//		private Optional<QName> manifestMainClassQName;

		public JvmFileOutputWriter(Reporter reporter, String modulePath, Optional<String> classDir) {
			super();
			this.reporter = reporter;
			this.modulePath = modulePath;
			this.classDir = classDir;
//			this.manifestMainClassQName = manifestMainClassQName;
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
//		@Override
//		public Optional<OutputStream> getOutputStream() {
//			return outputStream;
//		}
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

			QName manifestMainClassQName = moduleQName.child("$package$");
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
				
//				this.outputStream = Optional.of(out);
//				return Optional.of(out);
			} catch (IOException e) {
				reporter.error("Can't open to jar file " + jarFile.getAbsolutePath(), e);
			}
//			return Optional.empty();
//			OutputStream out = new BufferedOutputStream(new FileOutputStream(jarFile));
			
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
//			outputStream.ifPresent(os -> {try {
//				os.close();
//			} catch (IOException e) {
//				reporter.error("Can't close JAR file for module " + jarPathString, e);
//			}});
		}

		
	}
	public static class JvmByteOutputStreamWriter implements JvmOutputWriter {
		private Reporter reporter;
		private HashMap<QName /*class QName */, Bytecode> bytecodeMap;
//		private ByteArrayOutputStream byteArrayOutputStream; 
//		private Optional<OutputStream> optOutStream;

		public JvmByteOutputStreamWriter(Reporter reporter, HashMap<QName /*class QName */, Bytecode> bytecodeMap) {
			super();
			this.reporter = reporter;
			this.bytecodeMap = bytecodeMap;
//			this.byteArrayOutputStream = byteArrayOutputStream;
//			this.optOutStream = Optional.of(byteArrayOutputStream);
		}

//		@Override
//		public Optional<OutputStream> getOutputStream() {
//			return Optional.empty();
//		}

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
	
//	private Optional<JarOutputStream> outputStream = Optional.empty();
//	private Function<QName, Optional<OutputStream>> outputStreamBuilder;
	private JvmOutputWriter outputWriter;
	
	
	private JarCreatorListener(Reporter reporter, JvmOutputWriter outputWriter /* Function<QName, Optional<OutputStream>> outputStreamBuilder*/) {
//		this.outputStreamBuilder = outputStreamBuilder;
		this.reporter = reporter;
		this.outputWriter = outputWriter;
	}
//	/**
//	 * 
//	 * @param reporter
//	 * @param modulePath   module directory path, as given by '--module' option
//	 * @param moduleQName
//	 */
//	private JarCreatorListener(Reporter reporter, String modulePath, Optional<String> classDir) {
//		super();
//		this.reporter = reporter;
//		this.classDir = classDir;
//		this.outputWriter = new JvmFileOutputWriter(modulePath);
////		
////		this.outputStreamBuilder = (QName moduleQName) -> {
////
////			QName effectiveQName = moduleQName.isEmpty() ?
////					new QName("unnamed.jar") :
////					moduleQName.parent().get().child(moduleQName.getLast() + ".jar");
////				
////			this.jarPath = Paths.get(modulePath, effectiveQName.toArray());
////			
////			File jarFile = jarPath.toFile();
////
////			JarOutputStream os;
////			try {
////				File parentDir = jarFile.getParentFile();
////				if(parentDir!= null) {
////					parentDir.mkdirs();
////				}
////				if(jarFile.isFile()) {
////					jarFile.delete();
////				}
////				OutputStream out = new BufferedOutputStream(new FileOutputStream(jarFile));
////
//////				os = new JarOutputStream(out);
////				return Optional.of(out);
////			} catch (IOException e) {
////				reporter.error("Can't open to jar file " + jarFile.getAbsolutePath(), e);
////			}
////			return Optional.empty();
////		};
//	}

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
//		
//		
////		Optional<OutputStream> os = this.outputStreamBuilder.apply(moduleQName);
//		try {
//			
//			Manifest manifest = new Manifest();
//			Attributes attributes = manifest.getMainAttributes();
//			attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
//			attributes.put(Attributes.Name.MAIN_CLASS, "$package$");
////			attributes.put(Attributes.Name.MAIN_CLASS, "org.jpr.MainCompanion");
//			
//			this.outputWriter.getOutputStream().ifPresent(os -> {
//				
//				try {
//					this.outputStream = Optional.of(new JarOutputStream(os, manifest));
//				} catch (IOException e) {
//					reporter.error("Can't open JAR output file for module " + moduleQName, e);
//					e.printStackTrace();
//				}
//			});
////			if(os.isPresent()) {
////				this.outputStream = Optional.of(new JarOutputStream(os.get(), manifest));
////			}
//		} catch (Exception e) {
//			reporter.error("Can't open output jar file ", e);
//		}
	}

	@Override
	public void addByteCode(Bytecode bytecode, QName classQName) {
//		System.out.println(" ++ Adding bytecode to module " + modulePath + " : " + bytecode.size() + " bytes, to class: " + qName);
		
//		addToJar(bytecode, classQName);
		outputWriter.addToJar(bytecode, classQName);
		outputWriter.writeClassFile(bytecode, classQName);
//		writeClassFile(bytecode, classQName);
	}
	
//	private void addToJar(Bytecode bytecode, QName classQName) {
//		outputWriter.addToJar(bytecode, classQName);
////		this.outputStream.ifPresent(jarOS -> {
////			ZipEntry ze = new ZipEntry(classQName.slashSeparated() + ".class");
////			try {
////				jarOS.putNextEntry(ze);
////				jarOS.write(bytecode.getBytes());
////				jarOS.closeEntry();
////			} catch (IOException e) {
////				reporter.error("Can't write entry " + classQName + " to jar file " + this.outputWriter.getJarPathString() /* jarPath.toAbsolutePath()*/
////					+ ": " + e.getMessage(), e);
////			}
////		});
//	}
	
//	private void writeClassFile(Bytecode bytecode, QName classDeclarationQName ) {
//		outputWriter.writeClassFile(bytecode, classDeclarationQName);
////		classDir.ifPresent(cdir -> bytecode.createClassFiles(reporter, cdir, classDeclarationQName));
//	}
	

	@Override
	public void end() {
		outputWriter.close();
//		outputStream.ifPresent(os -> {
//			try {
//				os.close();
//			} catch (IOException e) {
//				reporter.error("Can't close to jar file " + jarPath.toAbsolutePath(), e);
//			}
//		});

	}

}
