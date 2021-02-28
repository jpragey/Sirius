package org.sirius.frontend.core;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;


import org.junit.jupiter.api.Test;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.BooleanType;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.parser.Compiler;

public class BooleanExpressionTest {

	
//	<T> T getInList(int pos, List<T> l) {
//		if(pos >= l.size())
//			throw new AssertionError("position " + pos + " >= size of list (" + l.size() + ")");
//		
//		T result = l.get(pos);
//		return result;
//	}
	
	@Test
	public void functionLocalBooleanVariableExpression() {
		ScriptSession session = Compiler.compileScript("#!\n "
				+ "void f(){Boolean b = true; }");

		List<AstModuleDeclaration> astModules = session.getAstModules();
		AstModuleDeclaration mod = astModules.get(0);
		AstPackageDeclaration pack = mod.getPackageDeclarations().get(0);
		FunctionDefinition func = pack.getFunctionDeclarations().get(0);
//		Partial partial = func.getAllArgsPartial();
		assertEquals(func.getBody().getStatementSize(), 1);
		
		AstLocalVariableStatement st = (AstLocalVariableStatement)func.getBody().getStatement(0);
//		AstLocalVariableStatement st = (AstLocalVariableStatement)getInList(0, func.getBody());
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
		LocalVariableStatement localVarST = (LocalVariableStatement)fd.getBodyStatements().get().get(0); 
//		assertEquals(localVarST.getName().getText(), "b");
////		assert(lvst.getType().toString(), "");
		Type t = localVarST.getType();
//		assertThat(t,  instanceOf(ClassDeclaration.class));
		assertThat(t,  instanceOf(BooleanType.class));
		
//		assert(t instanceof ClassDeclaration);
		
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
