package org.sirius.frontend.core.modules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.frontend.core.filestesting.InputFilesBuilder;
//import org.sirius.frontend.core.StandardFilesInputTest.InputFilesBuilder;

/** Test module/package organisation (files and embedded) 
 * 
 * @author jpragey
 *
 */
public class ModulesParsingTest {

	private Path tempDir;
	
	// ModuleBuilder created by test; they typically need after-test disk cleanup 
	private List<InputFilesBuilder> moduleBuilders = new ArrayList<>();

	@BeforeEach
	public void setup() throws IOException {
		this.tempDir = Files.createTempDirectory("sirius_tmp_" /*, attrs*/);
		tempDir.toFile().deleteOnExit();
	}
	@AfterEach
	public void tearDown() {
		moduleBuilders.forEach(mb -> mb.cleanupDisk());
		tempDir.toFile().delete();
		assertFalse(this.tempDir.toFile().exists());
	}

	@Test
	@DisplayName("Create a simple directory w/ module filesystem organization, check if it exists")
	public void createFileStructureTest() throws IOException {
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
				.withModuleDeclarator("module org.sirius.demo \"1.0.0\" {}\n")
				.withPackageDeclarator("package org.sirius.demo;\n")
				.withSource("a.sirius", "void fa() {/*a.sirius*/}\n")
				.withSource("b.sirius", "void fb() {/*b.sirius*/}\n")
				.create(tempDir);
			moduleBuilders.add(mb);
			
			assertTrue(this.tempDir.toFile().isDirectory());
			assertTrue(this.tempDir.resolve("org").toFile().isDirectory());
			assertTrue(this.tempDir.resolve("org/sirius/demo/").toFile().isDirectory());
			assertTrue(this.tempDir.resolve("org/sirius/demo/module.sirius").toFile().isFile());
			assertTrue(this.tempDir.resolve("org/sirius/demo/package.sirius").toFile().isFile());
			assertTrue(this.tempDir.resolve("org/sirius/demo/a.sirius").toFile().isFile());
			assertTrue(this.tempDir.resolve("org/sirius/demo/b.sirius").toFile().isFile());

	}
	
	
}
