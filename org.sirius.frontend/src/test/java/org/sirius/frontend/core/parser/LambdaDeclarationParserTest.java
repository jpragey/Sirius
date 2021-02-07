package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
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
	
	
	private LambdaDeclaration parseTypeDeclaration(String inputText /*, QName containerQName*/) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.lambdaDeclaration();
				
		LambdaDeclarationParser.LambdaDeclarationVisitor typeVisitor = new LambdaDeclarationParser.LambdaDeclarationVisitor(reporter /*, containerQName*/);
		LambdaDeclaration myType = typeVisitor.visit(tree);
		
		return myType;
	}

	@Test
	@DisplayName("Lambda parameters")
	@Disabled("Lambda support temp. removed")
	public void functionWithParameters() {
		LambdaDeclaration lambda = parseTypeDeclaration("void (A a, B b) {}");
		//assertEquals(partialList.getNameString(), "f");
		assertEquals(lambda.getArgs().size(), 2);
		
		assertThat(
				lambda.getArgs().stream().map(astFuncParam -> astFuncParam.getNameString()).toArray(),
				equalTo(new String[] {"a", "b"}));
	}

	@Test
	@DisplayName("Lambda Simple return type")
	public void functionReturnType() {
		LambdaDeclaration lambda = parseTypeDeclaration("Result () {}");
		AstType returnType = lambda.getReturnType();
		
		assertThat(returnType, instanceOf(SimpleType.class));
		
		assertEquals(((SimpleType)returnType).getNameString(), "Result");
	}
	
	@Test
	@DisplayName("Lambda with void return type")
	public void functionVoidReturnType() {
		LambdaDeclaration lambda = parseTypeDeclaration("void () {}"/*, new QName()*/);
		AstType returnType = lambda.getReturnType();
		
		assertThat(returnType, instanceOf(AstVoidType.class));
	}

//	@Test
//	@DisplayName("Lambda containing statements")
//	public void functionWithBodyStatements() {
//		LambdaDeclaration lambda = parseTypeDeclaration("void () {Integer i; return 42;}");
//		List<AstStatement> bodyStatements = lambda.getBody().get();
//		
//		assertThat(bodyStatements.size(), is(2));
//	}
}
