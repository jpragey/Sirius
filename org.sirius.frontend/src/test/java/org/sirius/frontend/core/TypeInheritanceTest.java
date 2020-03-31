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
	/**
	 * 	+ "package p.a;"
		+ "interface A(){}"	// TODO: interface
		+ "class D () implements A {}"

	 * @author jpragey
	 *
	 */
	public class ParseDualClassSource {
		List<AstClassDeclaration> astClasses;
		
		public ParseDualClassSource(String sourceCode) {
			ScriptSession session = Compiler.compileScript(sourceCode);
			List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
			assertEquals(moduleDeclarations.size(), 1);
			
			ModuleDeclaration md = session.getModuleDeclarations().get(0);
			PackageDeclaration pd = md.getPackages().get(1);
			
			List<ClassDeclaration> classes = pd.getClasses();
			assertEquals(classes.size(), 1);
			ClassDeclaration classD = classes.get(0);
			assertEquals(classD.getQName().dotSeparated(), "p.a.D");
//			assertEquals(classA.isInterface(), true);
			
			List<InterfaceDeclaration> interfaces = pd.getInterfaces();
			assertEquals(interfaces.size(), 1);

			InterfaceDeclaration classA = interfaces.get(0);
			assertEquals(classA.getQName().dotSeparated(), "p.a.A");
			
			////assertTrue(classA.isAncestorOrSame(classD));
			
			List<AstModuleDeclaration> astModules = session.getAstModules();
			List<AstPackageDeclaration> astPackages = astModules.get(0).getPackageDeclarations();
			AstPackageDeclaration astPack = astPackages.get(1);
			
			this.astClasses = astPack.getClassDeclarations();
			assertEquals(astClasses.size(), 2);
			
		}
	}
	
	@Test(description = "Check a class implementing an interface results in ClassDeclaration inheritance (ancestor declared before)")
	public void checkAstClassInheritanceWithAncestorEarlyDeclared() {
		TypeInheritanceTest.ParseDualClassSource code = new TypeInheritanceTest.ParseDualClassSource("#!\n "
				+ "package p.a;"
				+ "interface A(){}"	// TODO: interface
				+ "class D () implements A {}"
				);
		
		AstClassDeclaration astClassA = code.astClasses. get(0);
		assertEquals(astClassA.getAncestors().size(), 0);

		AstClassDeclaration astClassD = code.astClasses. get(1);
		assertEquals(astClassD.getAncestors().size(), 1);
	}

	@Test(description = "Check a class implementing an interface results in ClassDeclaration inheritance (ancestor declared after)")
	public void checkAstClassInheritanceWithAncestorLateDeclared() {
		TypeInheritanceTest.ParseDualClassSource code = new TypeInheritanceTest.ParseDualClassSource("#!\n "
				+ "package p.a;"
				+ "class D () implements A {}"
				+ "interface A(){}"
				);
		
		AstClassDeclaration astClassA = code.astClasses. get(1);
		assertEquals(astClassA.getAncestors().size(), 0);

		AstClassDeclaration astClassD = code.astClasses. get(0);
		assertEquals(astClassD.getAncestors().size(), 1);
	}

}
