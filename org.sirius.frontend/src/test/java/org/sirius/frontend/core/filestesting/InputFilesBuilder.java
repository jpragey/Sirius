package org.sirius.frontend.core.filestesting;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Builder for a real (on disk) file test bench 
 * 
 * @author jpragey
 *
 */
public class InputFilesBuilder {
	private List<String> modulePath;
	private Map<String, String> sourceFiles = new HashMap<>();
	private List<File> createdFiles = new ArrayList<>();
	private List<InputFilesBuilder> childrenBuilders = new ArrayList<>();

	public InputFilesBuilder(List<String> modulePath) {
		this.modulePath = modulePath;
	}
	public InputFilesBuilder(String ... modulePath) {
		this(List.of(modulePath));
	}
	public InputFilesBuilder withModuleDeclarator(String moduleDeclaratorContent) {
		return withSource("module.sirius", moduleDeclaratorContent);
	}
	public InputFilesBuilder withPackageDeclarator(String packageDeclaratorContent) {
		return withSource("package.sirius", packageDeclaratorContent);
	}
	public InputFilesBuilder withSource(String fileNameBase /* without '.sirius'*/,String fileContent) {
		assertFalse(sourceFiles.containsKey(fileNameBase), "duplicate file name: " + fileNameBase);
		sourceFiles.put(fileNameBase, fileContent);
		return this;
	}
	/**
	 * 
	 * @param contentBuilder
	 * @param relativePath
	 * @return this builder (NOT related to subdirectory)
	 */
	public InputFilesBuilder withSubDirectory(Consumer<InputFilesBuilder> contentBuilder, String ... relativePath) {
		List<String> pathElements = new ArrayList<>(List.of(relativePath));
		InputFilesBuilder subDirBuilder = new InputFilesBuilder(pathElements);
		contentBuilder.accept(subDirBuilder);
		childrenBuilders.add(subDirBuilder);
		return this;
	}

	public InputFilesBuilder create(Path rootPath) throws IOException {
		Path path = rootPath;

		for(String pathElem: modulePath) {
			path = path.resolve(pathElem);
			File dir = path.toFile();
			if(!dir.isDirectory()) {	// for subpackages starting with same path elements
				createdFiles.add(dir);
				assertTrue(dir.mkdir(), dir.toString());
			}
			//				System.out.println("Marking for deletion: " + dir);
		}
		for(Map.Entry<String, String> e: sourceFiles.entrySet()) {
			Path filePath = path.resolve(e.getKey());
			try(BufferedWriter writer =  Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);) {
				writer.append(e.getValue());
			}
			createdFiles.add(filePath.toFile());
		}
		for(InputFilesBuilder ifb: childrenBuilders) {
			ifb.create(path);
		};
		return this;
	}
	public void cleanupDisk() {
		List<InputFilesBuilder> cbs = new ArrayList<>(childrenBuilders);
		Collections.reverse(cbs);
		for(InputFilesBuilder ifb: cbs)
			ifb.cleanupDisk();

		List<File> filesToDelete = new ArrayList<File>(createdFiles);
		Collections.reverse(filesToDelete);
		for(File file: filesToDelete) {
			//				System.out.println("Cleanup: deletion of " + file);
			if(file.exists()) {
				if(!file.delete())
					throw new AssertionError("Couldn't delete temp. test file after test: " + file);
			}
		}
	}
}