package org.sirius.frontend.core;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.stdlayout.ModuleFiles;
import org.sirius.frontend.core.stdlayout.PackageFiles;


public class FrontEndTest {

	@Test
	@DisplayName("Standard compilation unit not implemented in new visitor-based parser")
	public void singleEmptyModuleMustHaveOneModuleContent() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		ModuleFiles mf = new ModuleFiles(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"), //StdInputTextProvider moduleDescriptor,
				List.of());
		StandardSession session = new StandardSession(reporter, List.of(mf));

		List<ModuleDeclaration> moduleContents = session.getModuleDeclarations();
		assertEquals(moduleContents.size(), 1);
		assertEquals(moduleContents.get(0).getPhysicalPath().getElements(), Arrays.asList("a", "b"));
	}
	
	@Test
	public void moduleWithoutExplicitPackageHasADefautltPackage() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		ModuleFiles mf = new ModuleFiles(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"), //StdInputTextProvider moduleDescriptor,
				List.of());
		StandardSession session = new StandardSession(reporter, List.of(mf));
		
		List<ModuleDeclaration> moduleContents = session.getModuleDeclarations();
		assertEquals(moduleContents.size(), 1);
		
		ModuleDeclaration mc = moduleContents.get(0);
		assertEquals(mc.getPackages().size(), 1);
		PackageDeclaration pd = mc.getPackages().get(0);
		
		assertEquals(pd.getQName().getStringElements(), Arrays.asList("a", "b"));
	}
	
	@Test
	public void moduleWithExplicitPackageHasNoDefautltPackage() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		ModuleFiles mf = new ModuleFiles(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"), //StdInputTextProvider moduleDescriptor,
				List.of(new PackageFiles(new TextInputTextProvider("a/b", "package.sirius", "package a.b;"), List.of())));
		StandardSession session = new StandardSession(reporter, List.of(mf));
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration mc = moduleDeclarations.get(0);
		assertEquals(1, mc.getPackages().size());
		PackageDeclaration pc = mc.getPackages().get(0);
		
		assertEquals(pc.getQName().getStringElements(), Arrays.asList("a", "b"));
	}
	
	
	@Test
	public void parseNamedModuleContent() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);

		ModuleFiles mf = new ModuleFiles(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"), //StdInputTextProvider moduleDescriptor,
				List.of(new PackageFiles(
						new TextInputTextProvider("a/b", "package.sirius", "package a.b;"), 
						List.of(
							new TextInputTextProvider("a/b", "A.sirius", "class A(){}"))
				)));

		StandardSession session = new StandardSession(reporter, List.of(mf));
		
		List<ModuleDeclaration> moduleContents = session.getModuleDeclarations();
		assertEquals(moduleContents.size(), 1);
		ModuleDeclaration module = moduleContents.get(0);

		List<PackageDeclaration> packageDeclarations = module.getPackages();
		assertEquals(packageDeclarations.size(), 1);
		
		
		List<PackageDeclaration> pkgs = module.getPackages();

		assertEquals(pkgs.size(), 1);
		PackageDeclaration pkg = pkgs.get(0);
		assertEquals(pkg.getQName().dotSeparated(), "a.b");
		
		/**
		assertEquals(cu0.getClassDeclarations().get(0).getName().getText(), "A");
		
		ModuleDescriptor mdcu = module.getModuleDescriptorCompilationUnit();
		assertEquals(mdcu.getModuleDeclaration().getqName().dotSeparated(), "a.b");
		*/
	}
	
}
