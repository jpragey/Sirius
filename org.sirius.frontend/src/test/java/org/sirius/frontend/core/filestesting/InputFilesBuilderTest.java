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
import org.junit.jupiter.api.io.TempDir;

public class InputFilesBuilderTest {
	private List<InputFilesBuilder> moduleBuilders = new ArrayList<>();


	/** Read the first line of a file, return null if PB */
	private String readFileLine(Path tempFile) throws IOException {
		BufferedReader reader = Files.newBufferedReader(tempFile, StandardCharsets.UTF_8);
		return reader.readLine();
	};

	@Test
	@DisplayName("Create a simple directory w/ module filesystem organization, check if it exists")
	public void createFileStructureTest(@TempDir Path tempDir) throws IOException {
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
				.withModuleDeclarator("module org.sirius.demo \"1.0.0\" {}\n")
				.withPackageDeclarator("package org.sirius.demo;\n")
				.withSource("a.sirius", "/* a.sirius */")
				.withSource("b.sirius", "/* b.sirius */")
				.create(tempDir);
			moduleBuilders.add(mb);
			
			assertTrue(tempDir.toFile().isDirectory());
			assertTrue(tempDir.resolve("org").toFile().isDirectory());
			assertTrue(tempDir.resolve("org/sirius/demo/").toFile().isDirectory());
			assertTrue(tempDir.resolve("org/sirius/demo/module.sirius").toFile().isFile());
			assertTrue(tempDir.resolve("org/sirius/demo/package.sirius").toFile().isFile());
			assertTrue(tempDir.resolve("org/sirius/demo/a.sirius").toFile().isFile());
			assertTrue(tempDir.resolve("org/sirius/demo/b.sirius").toFile().isFile());

			assertEquals("/* a.sirius */", readFileLine(tempDir.resolve("org/sirius/demo/a.sirius")));
			assertEquals("/* b.sirius */", readFileLine(tempDir.resolve("org/sirius/demo/b.sirius")));
			assertEquals("module org.sirius.demo \"1.0.0\" {}", readFileLine(tempDir.resolve("org/sirius/demo/module.sirius")));
			assertEquals("package org.sirius.demo;", readFileLine(tempDir.resolve("org/sirius/demo/package.sirius")));
	}
	
	@Test
	@DisplayName("Create a directory w/ module and a hierachy of packages, check if everything exist")
	public void createComplexStructureTest(@TempDir Path tempDir) throws IOException {
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
				.withSubDirectory(pkg10Builder ->{
					pkg10Builder
						.withSource("comp10.sirius", "/*comp10.sirius*/")
						.withPackageDeclarator("/* package org.sirius.demo.pkg1.pkg10 */")
						.withModuleDeclarator("/* module org.sirius.demo.pkg1.pkg10 */");
				}, "pkg1", "pkg10")
				.create(tempDir);
		
		assertTrue(tempDir.resolve("org/sirius/demo/pkg1").toFile().isDirectory());
		
		assertTrue(tempDir.resolve("org/sirius/demo/pkg1/pkg10").toFile().isDirectory());
		
		assertTrue(tempDir.resolve("org/sirius/demo/pkg1/pkg10/comp10.sirius").toFile().isFile());
		assertEquals("/*comp10.sirius*/", readFileLine(tempDir.resolve("org/sirius/demo/pkg1/pkg10/comp10.sirius")));
		
		// Package declarator
		assertTrue(tempDir.resolve("org/sirius/demo/pkg1/pkg10/package.sirius").toFile().isFile());
		assertEquals("/* package org.sirius.demo.pkg1.pkg10 */", readFileLine(tempDir.resolve("org/sirius/demo/pkg1/pkg10/package.sirius")));

		// Module declarator
		assertTrue(tempDir.resolve("org/sirius/demo/pkg1/pkg10/module.sirius").toFile().isFile());
		assertEquals("/* module org.sirius.demo.pkg1.pkg10 */", readFileLine(tempDir.resolve("org/sirius/demo/pkg1/pkg10/module.sirius")));

		moduleBuilders.add(mb);
	}
}
