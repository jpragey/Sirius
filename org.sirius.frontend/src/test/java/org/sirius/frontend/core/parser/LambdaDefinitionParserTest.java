package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.LambdaDeclaration;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.parser.Sirius;

public class LambdaDefinitionParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private LambdaDefinition parseLambdaDefinition(String inputText) {
		
//		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParserUtil.ParserFactory parserFactory = ParserUtil.createParserFactory(reporter, inputText);
		Sirius parser = parserFactory.create();

		ParseTree tree = parser.lambdaDefinition();
				
		LambdaDeclarationParser.LambdaDefinitionVisitor typeVisitor = new LambdaDeclarationParser(reporter, parserFactory.tokenStream()).new LambdaDefinitionVisitor();
		LambdaDefinition myType = typeVisitor.visit(tree);
		
		return myType;
	}

	@Test
	@DisplayName("Lambda parameters")
	@Disabled("Lambda support temp. removed")
	public void functionWithParameters() {
		LambdaDefinition lambda = parseLambdaDefinition("(A a, B b) {}");
		//assertEquals(partialList.getNameString(), "f");
		assertEquals(lambda.getArgs().size(), 2);
		
		assertThat(
				lambda.getArgs().stream().map(astFuncParam -> astFuncParam.getNameString()).toArray(),
				equalTo(new String[] {"a", "b"}));
	}

	@Test
	@DisplayName("Lambda Simple return type")
	public void functionReturnType() {
		LambdaDefinition lambda = parseLambdaDefinition("() : Result {}");
		AstType returnType = lambda.getReturnType();
		
		assertThat(returnType, instanceOf(SimpleType.class));
		
		assertEquals(((SimpleType)returnType).getNameString(), "Result");
	}
	
	@Test
	@DisplayName("Lambda with void return type")
	public void functionVoidReturnType() {
		LambdaDefinition lambda = parseLambdaDefinition(" () : void {}");
		AstType returnType = lambda.getReturnType();
		
		assertThat(returnType, instanceOf(AstVoidType.class));
	}

	@Test
	@DisplayName("Lambda without return type (void)")
	public void functionWihoutReturnType() {
		LambdaDefinition lambda = parseLambdaDefinition(" () {}");
		AstType returnType = lambda.getReturnType();
		
		assertThat(returnType, instanceOf(AstVoidType.class));
	}

	@Test
	@DisplayName("Lambda containing statements")
	public void functionWithBodyStatements() {
		LambdaDefinition lambda = parseLambdaDefinition("() : void {Integer i; return 42;}");
		List<AstStatement> bodyStatements = lambda.getBody().getStatements();
		
		assertThat(bodyStatements.size(), is(2));
	}

	@Test
	@DisplayName("Lambda definition is an expression")
	public void lambdaDefinitionAsExpression() {
		String inputText = "(A a) : void {}";
		
		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParserUtil.ParserFactory parserFactory = ParserUtil.createParserFactory(reporter, inputText);
//		Sirius parser = parserFactory.create();

		ParseTree tree = parser.expression();

		ExpressionParser.ExpressionVisitor v = new ExpressionParser(reporter, parserFactory.tokenStream()).new ExpressionVisitor();
		AstExpression lambdaExpr =  v.visit(tree);
		
		assertThat(lambdaExpr, notNullValue());
		assertThat(lambdaExpr, instanceOf(LambdaDefinition.class));
	}
}
