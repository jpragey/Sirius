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
import org.junit.jupiter.api.io.TempDir;
import org.sirius.frontend.core.filestesting.InputFilesBuilder;
//import org.sirius.frontend.core.StandardFilesInputTest.InputFilesBuilder;

/** Test module/package organisation (files and embedded) 
 * 
 * @author jpragey
 *
 */
public class ModulesParsingTest {

	// ModuleBuilder created by test; they typically need after-test disk cleanup 
	private List<InputFilesBuilder> moduleBuilders = new ArrayList<>();

	@Test
	@DisplayName("Create a simple directory w/ module filesystem organization, check if it exists")
	public void createFileStructureTest(@TempDir Path tempDir) throws IOException {
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
				.withModuleDeclarator("module org.sirius.demo \"1.0.0\" {}\n")
				.withPackageDeclarator("package org.sirius.demo;\n")
				.withSource("a.sirius", "void fa() {/*a.sirius*/}\n")
				.withSource("b.sirius", "void fb() {/*b.sirius*/}\n")
				.create(tempDir);
			moduleBuilders.add(mb);
			
			assertTrue(tempDir.toFile().isDirectory());
			assertTrue(tempDir.resolve("org").toFile().isDirectory());
			assertTrue(tempDir.resolve("org/sirius/demo/").toFile().isDirectory());
			assertTrue(tempDir.resolve("org/sirius/demo/module.sirius").toFile().isFile());
			assertTrue(tempDir.resolve("org/sirius/demo/package.sirius").toFile().isFile());
			assertTrue(tempDir.resolve("org/sirius/demo/a.sirius").toFile().isFile());
			assertTrue(tempDir.resolve("org/sirius/demo/b.sirius").toFile().isFile());

	}
	
	
}
