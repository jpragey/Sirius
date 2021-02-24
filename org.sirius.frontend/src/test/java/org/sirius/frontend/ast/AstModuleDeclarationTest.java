package org.sirius.frontend.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.parser.Compiler;

public class AstModuleDeclarationTest {

	@Test
	public void findPackageByQNameInNamedModuleTest() {
		ScriptSession session = Compiler.compileScript("#!\n module org.sirius.demo \"1.0.0\" {} package org.sirius.demo.p.k; void f(){}");
		QName pkgQName = new QName("org", "sirius", "demo", "p", "k");
		
		AstModuleDeclaration md = session.getAstModules().get(0);
		assertThat(md.getqName(), is(new QName("org", "sirius", "demo")));
		
		AstPackageDeclaration pd0 = md.getPackageDeclarations().get(0);
		assertThat(pd0.getQname(), is(pkgQName));

		assertThat(md.getPackage(pkgQName), is(notNullValue()));
		assertThat(md.getPackage(pkgQName).getFunctionDeclarations(), hasSize(1));
	}

	@Test
	public void findUnnamedPackageByQNameInNamedModuleTest() {
		ScriptSession session = Compiler.compileScript("#!\n module org.sirius.demo \"1.0.0\" {} void f(){}");
//		QName pkgQName = new QName("org", "sirius", "demo", "p", "k");
		
		AstModuleDeclaration md = session.getAstModules().get(0);
		assertThat(md.getqName(), is(new QName("org", "sirius", "demo")));
		
		AstPackageDeclaration pd0 = md.getPackageDeclarations().get(0);
		assertThat(pd0.getQname(), is(QName.empty));

		assertThat(md.getPackage(QName.empty), is(notNullValue()));
		assertThat(md.getPackage(QName.empty).getFunctionDeclarations(), hasSize(1));
	}
}
