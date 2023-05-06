package org.sirius.backend.jvm.integration;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;

public class JarCreationIT {

	private Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}

	@Test
	@DisplayName("When a JAR file is created on disk, it can be parsed and a module can be found there.")
	public void whenJarIsCreated_aModuleEntryCanBeFound(@TempDir Path tempdir) throws IOException {
		String script = "#!\n "
				+ "module a.b.c \"1.0\" {}\n"
				+ "class A(){}\n"
		+ "Integer main() {Integer a = 10; return a;}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));

		
		System.out.println("Creating a dir as " + tempdir);
		Path classFPath = tempdir.resolve("my_module.class");
		Path p = Files.createFile(classFPath);
		OutputStream out = Files.newOutputStream(p, StandardOpenOption.CREATE);

//		JvmOutputWriter ow = new JvmFileOutputWriter(reporter, modulePath, classDir);
//		return new JarCreatorListener(reporter, ow);

		backend.addFileOutput(script, Optional.of(tempdir.toAbsolutePath().toString() + "/UGLY"));
//		File jarFile = new File("/tmp/tmpJar/tmp.jar");
//		try {
//			File parentDir = jarFile.getParentFile();
//			if(parentDir!= null) {
//				parentDir.mkdirs();
//			}
//			if(jarFile.isFile()) {
//				jarFile.delete();
//			}
//			OutputStream out = new BufferedOutputStream(new FileOutputStream(jarFile));
//			
//			Manifest manifest = new Manifest();
//			Attributes attributes = manifest.getMainAttributes();
//			attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
////			attributes.put(Attributes.Name.MAIN_CLASS, "$package$");
//			attributes.put(Attributes.Name.MAIN_CLASS, "org.jpr.MainCompanion");
//			
//			JarOutputStream jarOS = new JarOutputStream(out, manifest);
//			
//			ZipEntry ze = new ZipEntry("a/b/c/Main.class");
////			ZipEntry ze = new ZipEntry("a.b.c.Main.class");
//			ZipEntry contZe = new ZipEntry("org");
//			writeZipEntry(jarOS, ze);
//
//			jarOS.close();
//			
//		} catch (Exception e) {
//			reporter.error("Can't open to jar file " + jarFile.getAbsolutePath(), e);
//		}
//		throw new UnsupportedOperationException();

	}
}
