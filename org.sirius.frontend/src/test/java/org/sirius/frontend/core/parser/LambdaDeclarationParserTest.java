package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.LambdaDeclaration;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.parser.SiriusParser;

public class LambdaDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private LambdaDeclaration parseLambdaDeclaration(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.lambdaDeclaration();
				
		LambdaDeclarationParser.LambdaDeclarationVisitor typeVisitor = new LambdaDeclarationParser.LambdaDeclarationVisitor(reporter);
		LambdaDeclaration myType = typeVisitor.visit(tree);
		
		return myType;
	}

	@Test
	@DisplayName("Lambda declaration")
//	@Disabled("Lambda support temp. removed")
	public void functionWithParameters() {
		LambdaDeclaration lambda = parseLambdaDeclaration("(A, B, C) -> Integer");
		//assertEquals(partialList.getNameString(), "f");
		assertThat(lambda.getArgTypes(), hasSize(3));

		assertThat(
				lambda.getArgTypes().stream().map(paramType -> paramType.messageStr()).toArray(),
				equalTo(new String[] {"class A<>", "class B<>", "class C<>"}));

		assertThat(
				lambda.getReturnType().messageStr(),
				is("class Integer<>"));
	}

//	@Test
//	@DisplayName("Lambda Simple return type")
//	public void functionReturnType() {
//		LambdaDefinition lambda = parseLambdaDefinition("Result () {}");
//		AstType returnType = lambda.getReturnType();
//		
//		assertThat(returnType, instanceOf(SimpleType.class));
//		
//		assertEquals(((SimpleType)returnType).getNameString(), "Result");
//	}
//	
//	@Test
//	@DisplayName("Lambda with void return type")
//	public void functionVoidReturnType() {
//		LambdaDefinition lambda = parseLambdaDefinition("void () {}"/*, new QName()*/);
//		AstType returnType = lambda.getReturnType();
//		
//		assertThat(returnType, instanceOf(AstVoidType.class));
//	}
//
//	@Test
//	@DisplayName("Lambda containing statements")
//	public void functionWithBodyStatements() {
//		LambdaDefinition lambda = parseLambdaDefinition("void () {Integer i; return 42;}");
//		List<AstStatement> bodyStatements = lambda.getBody().getStatements();
//		
//		assertThat(bodyStatements.size(), is(2));
//	}
}
