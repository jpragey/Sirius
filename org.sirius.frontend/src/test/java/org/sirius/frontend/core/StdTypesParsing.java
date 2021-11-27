package org.sirius.frontend.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.hamcrest.core.IsInstanceOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.frontend.api.BooleanConstantExpression;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FloatConstantExpression;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.parser.Compiler;

public class StdTypesParsing {

	@Test
	@DisplayName("An Integer constant type is api.Type.integerType")
	public void integerConstantTypeIsASingleton() {
		String script = "#!\n Integer main() {return 42;}";
		ScriptSession session = Compiler.compileScript(script);
		FunctionDefinition fd = session
			.getAstModules().get(0)
			.getPackageDeclarations().get(0)
			.getFunctionDeclarations().get(0);
		
		AstReturnStatement st =  (AstReturnStatement) fd.getBody().getStatement(0);
		AstExpression astExpr = st.getExpression();
		IntegerConstantExpression expr = (IntegerConstantExpression)astExpr.getExpression().get();
		
		assertThat(expr.type(), is(Type.integerType));
	}

	@Test
	@Disabled("Float not implemented")
	@DisplayName("A Float constant type is api.Type.floatType")
	public void floatConstantTypeIsASingleton() {
		String script = "#!\n Float main() {return 42.0;}";
		ScriptSession session = Compiler.compileScript(script);
		FunctionDefinition fd = session
			.getAstModules().get(0)
			.getPackageDeclarations().get(0)
			.getFunctionDeclarations().get(0);
		
		AstReturnStatement st =  (AstReturnStatement) fd.getBody().getStatement(0);
		AstExpression astExpr = st.getExpression();
		FloatConstantExpression expr = (FloatConstantExpression)astExpr.getExpression().get();
		
		assertThat(expr.type(), is(Type.floatType));
	}

	@Test
	@DisplayName("A Boolean constant type is api.Type.floatType")
	public void booleanConstantTypeIsASingleton() {
		String script = "#!\n Boolean main() {return true;}";
		ScriptSession session = Compiler.compileScript(script);
		FunctionDefinition fd = session
			.getAstModules().get(0)
			.getPackageDeclarations().get(0)
			.getFunctionDeclarations().get(0);
		
		AstReturnStatement st =  (AstReturnStatement) fd.getBody().getStatement(0);
		AstExpression astExpr = st.getExpression();
		BooleanConstantExpression expr = (BooleanConstantExpression)astExpr.getExpression().get();
		
		assertThat(expr.type(), is(Type.booleanType));
	}

}
