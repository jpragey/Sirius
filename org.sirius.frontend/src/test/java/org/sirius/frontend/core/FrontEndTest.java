package org.sirius.frontend.core;


import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;


public class FrontEndTest {

	// TODO: restore
	@Test
//	@Disabled
	@DisplayName("Standard compilation unit not implemented in new visitor-based parser")
	public void singleEmptyModuleMustHaveOneModuleContent() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}")
		));
		List<ModuleDeclaration> moduleContents = session.getModuleDeclarations();
		assertEquals(moduleContents.size(), 1);
		assertEquals(moduleContents.get(0).getPhysicalPath().getElements(), Arrays.asList("a", "b"));
	}
	
	@Test
	@Disabled
	public void moduleWithoutExplicitPackageHasADefautltPackage() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}")
		));
		List<ModuleDeclaration> moduleContents = session.getModuleDeclarations();
		assertEquals(moduleContents.size(), 1);
		
		ModuleDeclaration mc = moduleContents.get(0);
		assertEquals(mc.getPackages().size(), 1);
		PackageDeclaration pd = mc.getPackages().get(0);
		
//		assertEquals(pc.getPackageDeclaration().getPathElements(), Arrays.asList("a", "b"));
		assertEquals(pd.getQName().getStringElements(), Arrays.asList("a", "b"));
	}
	
	@Test
	@Disabled
	public void moduleWithExplicitPackageHasNoDefautltPackage() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"),
				new TextInputTextProvider("a/b", "package.sirius", "package a.b;")
		));
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
//		assertEquals(moduleContents.get(0).getModulePath().getElements(), Arrays.asList("a", "b"));
		ModuleDeclaration mc = moduleDeclarations.get(0);
		assertEquals(1, mc.getPackages().size());
		PackageDeclaration pc = mc.getPackages().get(0);
		
		assertEquals(pc.getQName().getStringElements(), Arrays.asList("a", "b"));
	}
	
	
	@Test
	@Disabled
	public void parseNamedModuleContent() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"),
				new TextInputTextProvider("a/b", "package.sirius", "package a.b;"),
				new TextInputTextProvider("a/b", "A.sirius", "class A(){}")
		));
		
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
		assertEquals(cu0.getClassDeclarations().get(0).getName().getText(), "A");
		
		
		
		ModuleDescriptor mdcu = module.getModuleDescriptorCompilationUnit();
		assertEquals(mdcu.getModuleDeclaration().getqName().dotSeparated(), "a.b");
		*/
	}
	
}
