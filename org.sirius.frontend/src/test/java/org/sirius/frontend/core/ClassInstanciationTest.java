package org.sirius.frontend.core;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ConstructorCall;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstExpressionStatement;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.ConstructorCallExpression;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.parser.Compiler;

public class ClassInstanciationTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	

	@Test
	@DisplayName("Class instanciation smoke test")
	public void classInstanciationSmokeTest() {
		String sourceCode = "void f(){ A(1,2,3); }";
		
		ScriptSession session = Compiler.compileScript(sourceCode);
		
		List<AstModuleDeclaration> moduleDeclarations = session.getAstModules();
		
		AstPackageDeclaration pd = moduleDeclarations.get(0).getPackageDeclarations().get(0);
		
		FunctionDefinition fd = pd.getFunctionDeclarations().get(0);
		
		AstStatement stmt0 = fd.getBody().getStatement(0);
		assertThat(stmt0, instanceOf(AstExpressionStatement.class));
		AstExpressionStatement exprStmt = (AstExpressionStatement)stmt0;
		
		AstExpression expr = exprStmt.getExpression();
		assertThat(expr, instanceOf(ConstructorCallExpression.class));
		ConstructorCallExpression ctorCallExpr = (ConstructorCallExpression)expr;
		
		assertThat(ctorCallExpr.getExpression().isPresent(), is(true));
		ConstructorCall apiExpr = (ConstructorCall)ctorCallExpr.getExpression().get();
		

//		AstStatement rawLambda = parseLambdaDefinition(
//				"(Integer, Integer) -> Integer add = (Integer a, Integer b) : Integer {};"
//				);
		
	}
}
