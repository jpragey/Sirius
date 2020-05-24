package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class BooleanExpressionTest {

	
	<T> T getInList(int pos, List<T> l) {
		if(pos >= l.size())
			throw new AssertionError("position " + pos + " >= size of list (" + l.size() + ")");
		
		T result = l.get(pos);
		return result;
	}
	
	@Test
	public void functionLocalBooleanVariableExpression() {
		ScriptSession session = Compiler.compileScript("#!\n "
				+ "void f(){Boolean b = true; }");

		List<AstModuleDeclaration> astModules = session.getAstModules();
		AstModuleDeclaration mod = astModules.get(0);
		AstPackageDeclaration pack = mod.getPackageDeclarations().get(0);
		AstFunctionDeclaration func = pack.getFunctionDeclarations().get(0);
		assertEquals(func.getStatements().size(), 1);
		
		AstLocalVariableStatement st = (AstLocalVariableStatement)getInList(0, func.getStatements());
		AstClassDeclaration type = (AstClassDeclaration)st.getType().resolve();
		
		assertEquals(type.getQName().dotSeparated(), "sirius.lang.Boolean");
		
		
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = moduleDeclarations.get(0);
		PackageDeclaration pd = md.getPackages().get(0);
		
		assertEquals(pd.getFunctions().size(), 1);
		AbstractFunction fd = pd.getFunctions().get(0);
		
		assertEquals(fd.getQName().getLast(), "f");
		assertEquals(fd.getBodyStatements().get().size(), 1);
//		Statement st = fd.getBodyStatements().get(0);
		LocalVariableStatement localVarST = (LocalVariableStatement)fd.getBodyStatements().get().get(0); 
//		assertEquals(localVarST.getName().getText(), "b");
////		assert(lvst.getType().toString(), "");
		Type t = localVarST.getType();
		assert(t instanceof ClassDeclaration);
		
//		assertEquals( ((AstClassDeclaration)t).getQName().dotSeparated(), "sirius.lang.Boolean");
//		assertEquals(lvst.getName().getText(), "b");
		
		
		// -- As API
		assertEquals(md.getPackages().size(), 1);
		PackageDeclaration apiPd = md.getPackages().get(0);
		
		assertEquals(apiPd.getFunctions().size(), 1);
		AbstractFunction tlf = apiPd.getFunctions().get(0);
		
		assertEquals(tlf.getQName().dotSeparated(), "f");
	}

}