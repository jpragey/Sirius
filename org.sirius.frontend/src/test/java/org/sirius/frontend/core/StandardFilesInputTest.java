package org.sirius.frontend.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.FailFastReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.filestesting.InputFilesBuilder;
import org.sirius.frontend.core.stdlayout.ModuleFiles;
import org.sirius.frontend.core.stdlayout.ModuleSourcesScanner;
import org.sirius.frontend.core.stdlayout.PackageFiles;
import org.sirius.frontend.core.stdlayout.StdInputTextProvider;

public class StandardFilesInputTest {

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
	public void testStandardModuleLayout() throws IOException {
//		System.out.println("Temp dir: " + tempDir);
		
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
			.withModuleDeclarator("module org.sirius.demo \"1.0.0\" {}\n")
			.withPackageDeclarator("package org.sirius.demo;\n")
			.withSource("a.sirius", "void fa() {/*a.sirius*/}\n")
			.withSource("b.sirius", "void fb() {/*b.sirius*/}\n")
			.create(tempDir);
		moduleBuilders.add(mb);

		Reporter reporter = new FailFastReporter(new ShellReporter());
		ModuleSourcesScanner scanner = new ModuleSourcesScanner(reporter, tempDir);
		List<ModuleFiles> moduleFiles = scanner.scan();
		assertThat(moduleFiles.size(), is(1));
		
		ModuleFiles mf = moduleFiles.get(0);
		assertThat(mf.getModuleDescriptor().getResourcePhysicalName(), is("module.sirius"));
		assertThat(mf.getModuleDescriptor().getText(), is("module org.sirius.demo \"1.0.0\" {}"));
		PackageFiles packageFiles0 = mf.getPackages().get(0);
		assertThat(packageFiles0.getPackageDescriptor().getResourcePhysicalName(), is("package.sirius"));
		assertThat(packageFiles0.getPackageDescriptor().getText(), is("package org.sirius.demo;"));
		
		assertThat(mf.getPackages().get(0).getSourceFiles().size(), is(2));
		
		InputTextProvider aTextProv = packageFiles0.getSourceFiles().stream()
				.filter(tp -> "a.sirius".equals(tp.getResourcePhysicalName()))
				.findAny()
				.get();
		assertThat(aTextProv.getText(), is("void fa() {/*a.sirius*/}"));
		assertThat(aTextProv.getInputLocation(), startsWith(tempDir.toString()));
		assertThat(aTextProv.getInputLocation(), endsWith("a.sirius"));
		
	}

	@Test
	public void testLayoutWithSubpackages() throws IOException {
//		System.out.println("Temp dir: " + tempDir);
		
		InputFilesBuilder mb = new InputFilesBuilder("org", "sirius", "demo")
			.withModuleDeclarator("module org.sirius.demo \"1.0.0\" {}\n")
			.withPackageDeclarator("package org.sirius.demo;\n")
			.withSource("a.sirius", "void fa() {/*a.sirius*/}\n")
			.withSubDirectory((InputFilesBuilder b) -> {
				b.withPackageDeclarator("package org.sirius.demo.pkga.pkgb0;\n")
				 .withSource("b0.sirius", "void f() {/*b0.sirius*/}\n");
			}, "pkga", "pkgb0")
			.withSubDirectory((InputFilesBuilder b) -> {
				b.withPackageDeclarator("package org.sirius.demo.pkga.pkgb1;\n")
				 .withSource("b1.sirius", "void f() {/*b1.sirius*/}\n");
			}, "pkga", "pkgb1")
			.create(tempDir);
		moduleBuilders.add(mb);

		Reporter reporter = new FailFastReporter(new ShellReporter());
		ModuleSourcesScanner scanner = new ModuleSourcesScanner(reporter, tempDir);
		List<ModuleFiles> moduleFiles = scanner.scan();
		assertThat(moduleFiles.size(), is(1));
		
		ModuleFiles mf = moduleFiles.get(0);
		assertThat(mf.getModuleDescriptor().getResourcePhysicalName(), is("module.sirius"));
		assertThat(mf.getModuleDescriptor().getText(), is("module org.sirius.demo \"1.0.0\" {}"));
		
		assertThat(mf.getPackages().size(), is(3));
		Map<String /*Res. physical name*/, PackageFiles> pkgMap = new HashMap<String, PackageFiles>();
		mf.getPackages().forEach(pkgFiles -> {pkgMap.put(pkgFiles.getPackageDescriptor().getPackagePhysicalName(), pkgFiles);});
//		System.out.println(pkgMap);
		assertThat(pkgMap.containsKey("org/sirius/demo"), is(true));
		assertThat(pkgMap.containsKey("org/sirius/demo/pkga/pkgb0"), is(true));
		assertThat(pkgMap.containsKey("org/sirius/demo/pkga/pkgb1"), is(true));
		
		PackageFiles packageFilesRoot = pkgMap.get("org/sirius/demo");
		assertNotNull(packageFilesRoot);
		assertThat(packageFilesRoot.getPackageDescriptor().getText(), is("package org.sirius.demo;"));
		
		PackageFiles packageFilesb0 = pkgMap.get("org/sirius/demo/pkga/pkgb0");
		assertNotNull(packageFilesb0);
		assertThat(packageFilesb0.getPackageDescriptor().getText(), is("package org.sirius.demo.pkga.pkgb0;"));
		
		PackageFiles packageFilesb1 = pkgMap.get("org/sirius/demo/pkga/pkgb1");
		assertNotNull(packageFilesb1);
		assertThat(packageFilesb1.getPackageDescriptor().getText(), is("package org.sirius.demo.pkga.pkgb1;"));
	}

}
