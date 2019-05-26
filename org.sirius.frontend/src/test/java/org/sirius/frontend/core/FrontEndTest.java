package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.ast.ModuleDescriptor;
import org.sirius.frontend.ast.PackageDeclaration;
import org.testng.annotations.Test;


public class FrontEndTest {

	@Test(enabled = true)
	public void singleEmptyModuleMustHaveOneModuleContent() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}")
		));
		List<ModuleContent> moduleContents = session.getModuleContents();
		assertEquals(moduleContents.size(), 1);
		assertEquals(moduleContents.get(0).getModulePath().getElements(), Arrays.asList("a", "b"));
	}
	
	@Test(enabled = false)
	public void moduleWithoutExplicitPackageHasADefautltPackage() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}")
		));
		List<ModuleContent> moduleContents = session.getModuleContents();
		assertEquals(moduleContents.size(), 1);
		
		ModuleContent mc = moduleContents.get(0);
		assertEquals(mc.getPackageContents().size(), 1);
		PackageDeclaration pd = mc.getPackageContents().get(0);
		
//		assertEquals(pc.getPackageDeclaration().getPathElements(), Arrays.asList("a", "b"));
		assertEquals(pd.getPathElements(), Arrays.asList("a", "b"));
	}
	
	@Test(enabled = true)
	public void moduleWithExplicitPackageHasNoDefautltPackage() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"),
				new TextInputTextProvider("a/b", "package.sirius", "package a.b;")
		));
		List<ModuleContent> moduleContents = session.getModuleContents();
		assertEquals(moduleContents.size(), 1);
		
//		assertEquals(moduleContents.get(0).getModulePath().getElements(), Arrays.asList("a", "b"));
		ModuleContent mc = moduleContents.get(0);
		assertEquals(mc.getPackageContents().size(), 1);
		PackageDeclaration pc = mc.getPackageContents().get(0);
		
		assertEquals(pc.getPathElements(), Arrays.asList("a", "b"));
	}
	
	
	@Test(enabled = true)
	public void parseNamedModuleContent() {
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
 
		FrontEnd frontEnd = new FrontEnd(reporter);
		StandardSession session = frontEnd.createStandardSession(Arrays.asList(
				new TextInputTextProvider("a/b", "module.sirius", "module a.b \"1\" {}"),
				new TextInputTextProvider("a/b", "package.sirius", "package a.b;"),
				new TextInputTextProvider("a/b", "A.sirius", "class A(){}")
		));
		
		List<ModuleContent> moduleContents = session.getModuleContents();
		assertEquals(moduleContents.size(), 1);
		ModuleContent module = moduleContents.get(0);

		List<PackageDeclaration> packageDeclarations = module.getPackageContents();
		assertEquals(packageDeclarations.size(), 1);
		
		
		List<PackageDeclaration> pkgs = module.getModuleDeclaration().getPackageDeclarations();

		assertEquals(pkgs.size(), 1);
		PackageDeclaration pkg = pkgs.get(0);
		assertEquals(pkg.getQnameString(), "a.b");
		
		/**
		assertEquals(cu0.getClassDeclarations().get(0).getName().getText(), "A");
		assertEquals(cu0.getClassDeclarations().get(0).getName().getText(), "A");
		
		
		
		ModuleDescriptor mdcu = module.getModuleDescriptorCompilationUnit();
		assertEquals(mdcu.getModuleDeclaration().getqName().dotSeparated(), "a.b");
		*/
	}
	
}
