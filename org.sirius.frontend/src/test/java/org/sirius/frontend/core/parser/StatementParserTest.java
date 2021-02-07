package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstBooleanConstantExpression;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstExpressionStatement;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstIfElseStatement;
import org.sirius.frontend.ast.AstIntegerConstantExpression;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.parser.SiriusParser;

public class StatementParserTest {

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
	@DisplayName("Simple return statement")
	public void returnStatement() {
		parseReturnStatement("return 42;",      (retStmt -> {
			AstExpression e = retStmt.getExpression();
			assertThat(e, instanceOf(AstIntegerConstantExpression.class));
			assertThat(((AstIntegerConstantExpression)e).getValue(), equalTo(42));
		}) );
		parseReturnStatement("return x;",      (retStmt -> {
			AstExpression e = retStmt.getExpression();
			assertThat(e, instanceOf(SimpleReferenceExpression.class));
			assertThat(((SimpleReferenceExpression)e).getNameString(), equalTo("x"));
		}) );
	}

	private AstReturnStatement parseReturnStatement(String inputText, Consumer<AstReturnStatement> verify) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.returnStatement();
		
		StatementParser.StatementVisitor visitor = new StatementParser.StatementVisitor(reporter);
		AstStatement stmt = visitor.visit(tree);

		assertThat(stmt, instanceOf(AstReturnStatement.class) );
		AstReturnStatement e = ((AstReturnStatement)stmt);
		verify.accept(e);
		return e;
	}
	@Test
	@DisplayName("Simple expression statement")
	public void expressionStatement() {
		parseExpressionStatement("42;",      (retStmt -> {
			AstExpression e = retStmt.getExpression();
			assertThat(e, instanceOf(AstIntegerConstantExpression.class));
			assertThat(((AstIntegerConstantExpression)e).getValue(), equalTo(42));
		}) );
		parseExpressionStatement("f(42);",      (retStmt -> {
			AstExpression e = retStmt.getExpression();
			assertThat(e, instanceOf(AstFunctionCallExpression.class));
			assertThat(((AstFunctionCallExpression)e).getNameString(), equalTo("f"));
		}) );
	}

	private AstExpressionStatement parseExpressionStatement(String inputText, Consumer<AstExpressionStatement> verify) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.statement();
		
		StatementParser.StatementVisitor visitor = new StatementParser.StatementVisitor(reporter);
		AstStatement stmt = visitor.visit(tree);

		assertThat(stmt, instanceOf(AstExpressionStatement.class) );
		AstExpressionStatement e = ((AstExpressionStatement)stmt);
		verify.accept(e);
		return e;
	}

	@Test
	@DisplayName("Local variable statement")
	public void localVariableStatement() {
		parseLocalVariableStatement("Integer x;",      (locVarStmt -> {
			assertThat(locVarStmt.getVarName().getText(), equalTo("x"));
			AstType type = locVarStmt.getType();
			assertThat(type, instanceOf(SimpleType.class));
			SimpleType simpleType = (SimpleType)type;
			assertThat(simpleType.getNameString(), equalTo("Integer"));
			assertThat(locVarStmt.getInitialValue().isEmpty(), is(true));
		}) );
		parseLocalVariableStatement("Integer x = 42;",      (locVarStmt -> {
			assertThat(locVarStmt.getInitialValue().isPresent(), is(true));
			AstExpression init = locVarStmt.getInitialValue().get();
			assertThat(init, instanceOf(AstIntegerConstantExpression.class));
			assertThat(((AstIntegerConstantExpression)init).getValue(), equalTo(42));
		}) );
	}

	private AstLocalVariableStatement parseLocalVariableStatement(String inputText, Consumer<AstLocalVariableStatement> verify) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.statement();
		
		StatementParser.StatementVisitor visitor = new StatementParser.StatementVisitor(reporter);
		AstStatement stmt = visitor.visit(tree);

		assertThat(stmt, instanceOf(AstLocalVariableStatement.class) );
		AstLocalVariableStatement e = ((AstLocalVariableStatement)stmt);
		verify.accept(e);
		return e;
	}

	@Test
	@DisplayName("Local variable statement")
	public void ifElseStatement() {
		parseIfElseStatement("if(true) f(1);",      (locVarStmt -> {
			assertThat(locVarStmt.getIfExpression(), instanceOf(AstBooleanConstantExpression.class));
			assertThat(locVarStmt.getIfBlock(), instanceOf(AstExpressionStatement.class));
			assertThat(locVarStmt.getElseBlock().isPresent(), equalTo(false));
		}) );
		parseIfElseStatement("if(true) f(1); else g(2);",      (locVarStmt -> {
			assertThat(locVarStmt.getIfExpression(), instanceOf(AstBooleanConstantExpression.class));
			assertThat(locVarStmt.getIfBlock(), instanceOf(AstExpressionStatement.class));
			assertThat(locVarStmt.getElseBlock().isPresent(), equalTo(true));
			assertThat(locVarStmt.getElseBlock().get(), instanceOf(AstExpressionStatement.class));
		}) );
	}

	private AstIfElseStatement parseIfElseStatement(String inputText, Consumer<AstIfElseStatement> verify) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.statement();
		
		StatementParser.StatementVisitor visitor = new StatementParser.StatementVisitor(reporter);
		AstStatement stmt = visitor.visit(tree);

		assertThat(stmt, instanceOf(AstIfElseStatement.class) );
		AstIfElseStatement e = ((AstIfElseStatement)stmt);
		verify.accept(e);
		return e;
	}

}
