package org.sirius.frontend.core;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.sirius.frontend.ast.AstBooleanConstantExpression;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstIfElseStatement;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.parser.Compiler;

public class IfElseTest {

	@Test
	public void classTest() throws Exception {
		String script = "#!\n "
//				+ "class A() {Integer mi;}   "
				+ "Integer main() {if(true) return 42; return 43;}";
		
		
		ScriptSession session = Compiler.compileScript(script);
		
		List<AstModuleDeclaration> moduleDeclarations = session.getAstModules();
		assertEquals(moduleDeclarations.size(), 1);
		
		AstModuleDeclaration md = moduleDeclarations.get(0);
		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
		assertEquals(pd.getFunctionDeclarations().size(), 1);
		FunctionDefinition fd = pd.getFunctionDeclarations().get(0);
		assertEquals(fd.getqName().dotSeparated(), "main");

		AstIfElseStatement ifElse = (AstIfElseStatement) fd.getBody().get(0);
		AstExpression ifExpr = ifElse.getIfExpression();
		
		assertEquals(ifExpr.getClass(), AstBooleanConstantExpression.class);
		assertEquals(ifElse.getIfBlock().getClass(), AstReturnStatement.class);
		assertEquals(ifElse.getElseBlock().isPresent(), false);
		
	}

}
