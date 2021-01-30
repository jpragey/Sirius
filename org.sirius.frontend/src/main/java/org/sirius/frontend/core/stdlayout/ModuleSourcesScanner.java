package org.sirius.frontend.core.stdlayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.sirius.common.error.Reporter;

public class ModuleSourcesScanner {
	private Path sourceDirPath;
	private Reporter reporter;

	public ModuleSourcesScanner(Reporter reporter, Path sourceDirPath) {
		super();
		this.sourceDirPath = sourceDirPath;
		this.reporter = reporter;
	}
	public List<ModuleFiles> scan() {
		List<ModuleFiles> currentModuleFiles = new ArrayList<>();
		scanDirForModules(Collections.emptyList(), sourceDirPath, currentModuleFiles);

		return currentModuleFiles;
	}
	private Optional<StdInputTextProvider> createTextProvider(File file, List<String> pathElements, Consumer<StdInputTextProvider> handler) {
		StdInputTextProvider textProvider;
		try {
			textProvider = StdInputTextProvider.sourceFile(file, pathElements);
			handler.accept(textProvider);
			return Optional.of(textProvider);
		} catch (IOException e) {
			reporter.error("Can't read file " + file.getAbsolutePath(), e);
		}
		return Optional.empty();
	}
	private void scanDirForPackages(List<String> pathElements, Path path, List<PackageFiles> packageFiles, boolean inModuleRoot) {
		File[] content = path.toFile().listFiles();
		Optional<StdInputTextProvider> optPackageDescriptor = Optional.empty();
		List<StdInputTextProvider> sourceFiles = new ArrayList<StdInputTextProvider>();
		
		for(File file: content) {
			String fname = file.getName();
			if(file.isFile()) {
				if(fname.equals("module.sirius")) {
					if(! inModuleRoot) {
						reporter.error("Nested modules not allowed: " + path);
					} // else ignore
				}
				else if(fname.equals("package.sirius")) {
					optPackageDescriptor = createTextProvider(file, pathElements, (textProvider) -> {});
				}
				else if(fname.endsWith(".sirius")){
					createTextProvider(file, pathElements, (StdInputTextProvider textProvider) -> {sourceFiles.add(textProvider);});
				} else {
					reporter.warning("File ignored (source file names should have a '.sirius' extension: " + fname);
				}
			} else if(file.isDirectory()) {
				List<String> childPElts = new ArrayList<String>(pathElements);
				childPElts.add(fname);
				scanDirForPackages(childPElts, file.toPath(), packageFiles, false /*inModuleRoot*/);
			}
		}
		optPackageDescriptor.ifPresent(itp -> {
			packageFiles.add(new PackageFiles(itp, sourceFiles));
		});
		if(optPackageDescriptor.isEmpty()) {
			if(!sourceFiles.isEmpty()) {
				reporter.error("Directory " + path + " contains source files but no package descriptor (package.sirius).");
			}
		}
	}
	private void scanDirForModules(List<String> pathElements, Path path, List<ModuleFiles> currentModuleFiles/* Collected results */) {
		File[] content = path.toFile().listFiles();
		Optional<StdInputTextProvider> optModuleDescriptor = Optional.empty();
		for(File file: content) {
			String fname = file.getName();
			if(file.isFile()) {
				if(fname.equals("module.sirius")) {
					optModuleDescriptor = createTextProvider(file, pathElements, (textProvider) -> {});
					List<PackageFiles> packages = new ArrayList<PackageFiles>();

					scanDirForPackages(pathElements, path, packages, true /*inModuleRoot*/);


					ModuleFiles mf = new ModuleFiles(optModuleDescriptor.get()/*TODO CHECK*/, packages);
					currentModuleFiles.add(mf);
				}
			} else if(file.isDirectory()) {
				ArrayList<String> childPathElements = new ArrayList<>(pathElements);
				childPathElements.add(file.getName());
				scanDirForModules(childPathElements, file.toPath(), currentModuleFiles);
			}
		}
	}
}