package org.sirius.backend.jvm.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JarCreatorListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.ScriptSession;

public class PackageTest {

	private AccumulatingReporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter();
	}

	@Test
	public void nestedPackageTests() throws Exception {
		String script = "#!\n "
				+ "package a.b.c ;"
//				+ "module org.mod  \"1.0\" {} "
				+ "class A() {} "
//				+ "A main() {return A();}"
				+ "void jvmMain(){}"
				;
		
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		BackendOptions backendOptions = new BackendOptions(reporter, Optional.of("a.b.c.jvmMain") /* jvmMain option*/);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, backendOptions);

		JarCreatorListener jarOutput = backend.addFileOutput("modules" /*modulePath*/, Optional.empty() /* <String> classDir*/);
		
		backend.process(session);
		
//		this.reporter.rethrowFirst();
		assertThat(
				this.reporter.getErrors().isEmpty() ? "" : this.reporter.getErrors().get(0),
				this.reporter.hasErrors(), is(false));

	}
	
	@Test
	@Disabled("TODO:remove")
	@DisplayName("TEMP: Try to create a jar file with nested entries.")
	public void dummyJarCreation() {
		File jarFile = new File("/tmp/tmpJar/tmp.jar");
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
//			attributes.put(Attributes.Name.MAIN_CLASS, "$package$");
			attributes.put(Attributes.Name.MAIN_CLASS, "org.jpr.MainCompanion");
			
			JarOutputStream jarOS = new JarOutputStream(out, manifest);
			
			ZipEntry ze = new ZipEntry("a/b/c/Main.class");
//			ZipEntry ze = new ZipEntry("a.b.c.Main.class");
			ZipEntry contZe = new ZipEntry("org");
			writeZipEntry(jarOS, ze);

			jarOS.close();
			
		} catch (Exception e) {
			reporter.error("Can't open to jar file " + jarFile.getAbsolutePath(), e);
		}

	}
	
	private void writeZipEntry(JarOutputStream jarOS, ZipEntry ze) {
		try {
			jarOS.putNextEntry(ze);
			jarOS.write(new byte[] {1,2,3});
			jarOS.closeEntry();
		} catch (IOException e) {
			reporter.error("Can't write entry : " + e.getMessage(), e);
		}
		
	}
	
	
}
