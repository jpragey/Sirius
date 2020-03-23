package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class TypeInheritanceTest {

	@Test(description = "Check a class implementing an interface results in ClassDeclaration inheritance")
	public void checkStringFunctionParameterIsASiriusLangString() {
		ScriptSession session = Compiler.compileScript("#!\n "
				+ "package p.a;"
				+ "interface A(){}"	// TODO: interface
				+ "class D () implements A {}"
				+ ""
				+ "void f(String s){}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		PackageDeclaration pd = md.getPackages().get(1);
		
		List<ClassDeclaration> classes = pd.getClasses();
		assertEquals(classes.size(), 1);
		ClassDeclaration classD = classes.get(0);
		assertEquals(classD.getQName().dotSeparated(), "p.a.D");
//		assertEquals(classA.isInterface(), true);
		
		List<InterfaceDeclaration> interfaces = pd.getInterfaces();
		assertEquals(interfaces.size(), 1);

		InterfaceDeclaration classA = interfaces.get(0);
		assertEquals(classA.getQName().dotSeparated(), "p.a.A");
		
		////assertTrue(classA.isAncestorOrSame(classD));
		
		List<AstModuleDeclaration> astModules = session.getAstModules();
		List<AstPackageDeclaration> astPackages = astModules.get(0).getPackageDeclarations();
		AstPackageDeclaration astPack = astPackages.get(1);
		
		List<AstClassDeclaration> astClasses = astPack.getClassDeclarations();
		assertEquals(astClasses.size(), 2);
		
		AstClassDeclaration astClassA = astClasses.get(0);
		assertEquals(astClassA.getAncestors().size(), 0);

		AstClassDeclaration astClassD = astClasses.get(1);
		assertEquals(astClassD.getAncestors().size(), 1);
		
		assertTrue(astClassA.isAncestorOrSameAs(astClassD));
		assertFalse(astClassD.isAncestorOrSameAs(astClassA));
	}

}
