package org.sirius.frontend.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.Compiler;


public class TypeInheritanceTest {
	/**
	 * 	+ "package p.a;"
		+ "interface I(){}"	// TODO: interface
		+ "class C () implements I {}"

	 * @author jpragey
	 *
	 */
	public class ParseDualClassSource {
		List<AstClassDeclaration> astClasses;
		List<AstInterfaceDeclaration> astInterfaces;
		
		public ParseDualClassSource(String sourceCode) {
			ScriptSession session = Compiler.compileScript(sourceCode);
			List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
			assertEquals(moduleDeclarations.size(), 1);
			
			ModuleDeclaration md = session.getModuleDeclarations().get(0);
			PackageDeclaration pd = md.packageDeclarations().get(0);
			
			List<ClassType> classes = pd.getClasses();
			assertEquals(classes.size(), 1);
			ClassType classD = classes.get(0);
			assertEquals(classD.getQName().dotSeparated(), "p.a.C");
//			assertEquals(classA.isInterface(), true);
			
			List<ClassType> interfaces = pd.getInterfaces();
			assertEquals(interfaces.size(), 1);

			ClassType classA = interfaces.get(0);
			assertEquals(classA.getQName().dotSeparated(), "p.a.I");
			
			////assertTrue(classA.isAncestorOrSame(classD));
			
			List<AstModuleDeclaration> astModules = session.getAstModules();
			List<AstPackageDeclaration> astPackages = astModules.get(0).getPackageDeclarations();
			AstPackageDeclaration astPack = astPackages.get(0);
			
			this.astClasses = astPack.getClassDeclarations();
			assertEquals(astClasses.size(), 1);
			this.astInterfaces = astPack.getInterfaceDeclarations();
			assertEquals(astClasses.size(), 1);
			
		}
	}
	
	@Test
	@DisplayName("Check a class implementing an interface results in ClassDeclaration inheritance (ancestor declared before)")
	public void checkAstClassInheritanceWithAncestorEarlyDeclared() {
		TypeInheritanceTest.ParseDualClassSource code = new TypeInheritanceTest.ParseDualClassSource("#!\n "
				+ "package p.a;"
				+ "interface I{}"	// TODO: interface
				+ "class C () implements I {}"
				);
		
		AstClassDeclaration astClassC = code.astClasses. get(0);
		assertEquals(astClassC.getAncestors().size(), 1);
		
		assertEquals(astClassC.getAncestors().get(0).getText(), "I");

		AstInterfaceDeclaration astClassI = code.astInterfaces. get(0);
		assertEquals(astClassI.getAncestors().size(), 0);
		
		// -- Check API 
		ClassType apiC = astClassC.getClassDeclaration();
		assertThat(apiC.getQName().dotSeparated(), is("p.a.C"));

		ClassType apiI = astClassI.getInterfaceDeclaration();
		assertThat(apiI.getQName().dotSeparated(), is("p.a.I"));
		
		assertThat(astClassC.getInterfaces(), hasSize(1));
//		assertThat(apiC.getInterface(), hasSize(1));
		assertThat(astClassC.getInterfaces().get(0).getQName().dotSeparated(), is("p.a.I"));
	}

	@Test
	@DisplayName("Check a class implementing an interface results in ClassDeclaration inheritance (ancestor declared after)")
	public void checkAstClassInheritanceWithAncestorLateDeclared() {
		TypeInheritanceTest.ParseDualClassSource code = new TypeInheritanceTest.ParseDualClassSource("#!\n "
				+ "package p.a;"
				+ "class C () implements I {}"
				+ "interface I {}"
				);
		
		AstClassDeclaration astClassC = code.astClasses. get(0);
		assertEquals(astClassC.getAncestors().size(), 1);
		
		assertEquals(astClassC.getAncestors().get(0).getText(), "I");

		AstInterfaceDeclaration astClassI = code.astInterfaces. get(0);
		assertEquals(astClassI.getAncestors().size(), 0);
	}

}
