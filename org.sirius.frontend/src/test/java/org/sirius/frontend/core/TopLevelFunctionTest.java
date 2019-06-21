package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class TopLevelFunctionTest {

	@Test
	public void findTopLevelFunction() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  Void f(){}");

		assertEquals(session.getModuleContents().size(), 1);
		
		AstModuleDeclaration md = session.getModuleContents().get(0).getModuleDeclaration();
		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
		
		assertEquals(pd.getFunctionDeclarations().size(), 1);
		AstFunctionDeclaration fd = pd.getFunctionDeclarations().get(0);
		
		assertEquals(fd.getName().getText(), "f");
		
		// -- As API
		List<ModuleDeclaration> mds = session.getModuleDeclarations();
		assertEquals(mds.size(), 1);
		assertEquals(mds.get(0).getPackages().size(), 1);
		PackageDeclaration apiPd = mds.get(0).getPackages().get(0);
		
		assertEquals(apiPd.getFunctions().size(), 1);
		TopLevelFunction tlf = apiPd.getFunctions().get(0);
		
		assertEquals(tlf.getQName().dotSeparated(), "a.b.f");
	}

	@Test(description = "")
	public void checkFunctionArgumentsInAPIFunction() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  Void f(Integer i, Integer j){}");

		AstModuleDeclaration md = session.getModuleContents().get(0).getModuleDeclaration();
		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
		AstFunctionDeclaration fd = pd.getFunctionDeclarations().get(0);

	}

}
