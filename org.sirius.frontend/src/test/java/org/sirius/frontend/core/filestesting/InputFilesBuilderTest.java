package org.sirius.frontend.core.filestesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InputFilesBuilderTest {
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

	/** Read the first line of a file, return null if PB */
	private String readFileLine(String rPath /*relative to this.tempDir*/) throws IOException {
		BufferedReader reader = Files.newBufferedReader(this.tempDir.resolve(rPath), StandardCharsets.UTF_8);
		return reader.readLine();
	};

	@Test
	@DisplayName("Create a simple directory w/ module filesystem organization, check if it exists")
	public void createFileStructureTest() throws IOException {
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
				.withModuleDeclarator("module org.sirius.demo \"1.0.0\" {}\n")
				.withPackageDeclarator("package org.sirius.demo;\n")
				.withSource("a.sirius", "/* a.sirius */")
				.withSource("b.sirius", "/* b.sirius */")
				.create(tempDir);
			moduleBuilders.add(mb);
			
			assertTrue(this.tempDir.toFile().isDirectory());
			assertTrue(this.tempDir.resolve("org").toFile().isDirectory());
			assertTrue(this.tempDir.resolve("org/sirius/demo/").toFile().isDirectory());
			assertTrue(this.tempDir.resolve("org/sirius/demo/module.sirius").toFile().isFile());
			assertTrue(this.tempDir.resolve("org/sirius/demo/package.sirius").toFile().isFile());
			assertTrue(this.tempDir.resolve("org/sirius/demo/a.sirius").toFile().isFile());
			assertTrue(this.tempDir.resolve("org/sirius/demo/b.sirius").toFile().isFile());

			assertEquals("/* a.sirius */", readFileLine("org/sirius/demo/a.sirius"));
			assertEquals("/* b.sirius */", readFileLine("org/sirius/demo/b.sirius"));
			assertEquals("module org.sirius.demo \"1.0.0\" {}", readFileLine("org/sirius/demo/module.sirius"));
			assertEquals("package org.sirius.demo;", readFileLine("org/sirius/demo/package.sirius"));
	}
	
	@Test
	@DisplayName("Create a directory w/ module and a hierachy of packages, check if everything exist")
	public void createComplexStructureTest() throws IOException {
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
				.withSubDirectory(pkg10Builder ->{
					pkg10Builder
						.withSource("comp10.sirius", "/*comp10.sirius*/")
						.withPackageDeclarator("/* package org.sirius.demo.pkg1.pkg10 */")
						.withModuleDeclarator("/* module org.sirius.demo.pkg1.pkg10 */");
				}, "pkg1", "pkg10")
				.create(tempDir);
		
		assertTrue(this.tempDir.resolve("org/sirius/demo/pkg1").toFile().isDirectory());
		
		assertTrue(this.tempDir.resolve("org/sirius/demo/pkg1/pkg10").toFile().isDirectory());
		
		assertTrue(this.tempDir.resolve("org/sirius/demo/pkg1/pkg10/comp10.sirius").toFile().isFile());
		assertEquals("/*comp10.sirius*/", readFileLine("org/sirius/demo/pkg1/pkg10/comp10.sirius"));
		
		// Package declarator
		assertTrue(this.tempDir.resolve("org/sirius/demo/pkg1/pkg10/package.sirius").toFile().isFile());
		assertEquals("/* package org.sirius.demo.pkg1.pkg10 */", readFileLine("org/sirius/demo/pkg1/pkg10/package.sirius"));

		// Module declarator
		assertTrue(this.tempDir.resolve("org/sirius/demo/pkg1/pkg10/module.sirius").toFile().isFile());
		assertEquals("/* module org.sirius.demo.pkg1.pkg10 */", readFileLine("org/sirius/demo/pkg1/pkg10/module.sirius"));

		moduleBuilders.add(mb);
	}
}
