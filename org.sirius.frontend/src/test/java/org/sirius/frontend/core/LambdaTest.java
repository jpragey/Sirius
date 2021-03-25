package org.sirius.frontend.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher.CannotInvokeStartRule;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.LambdaDeclaration;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.core.parser.ParserUtil;
import org.sirius.frontend.core.parser.StatementParser;
import org.sirius.frontend.parser.SiriusParser;

public class LambdaTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstStatement parseLambdaDefinition(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.localVariableStatement();
				
		StatementParser.StatementVisitor typeVisitor = new StatementParser.StatementVisitor(reporter);
		AstStatement myType = typeVisitor.visit(tree);
		
		return myType;
	}

	@Test
	@DisplayName("Lambda smoke test")
	public void lambdaSmokeTest() {
		AstStatement rawLambda = parseLambdaDefinition("(A, B) -> Integer add = (A a, B b) : Integer {};");
		
		assertThat(rawLambda, instanceOf(AstLocalVariableStatement.class));
		AstLocalVariableStatement var = (AstLocalVariableStatement)rawLambda;
		
		assertThat(var.getVarName().getText(), is("add"));
		
		assertThat(var.getType(), instanceOf(LambdaDeclaration.class));
		LambdaDeclaration varType = (LambdaDeclaration)var.getType();

		assertThat(var.getInitialValue().get(), instanceOf(LambdaDefinition.class));
		LambdaDefinition initValue = (LambdaDefinition)var.getInitialValue().get();

	}
}
