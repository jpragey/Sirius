package org.sirius.frontend.core.parser;

import static org.testng.Assert.assertEquals;

import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.ast.AstBinaryOpExpression;
import org.sirius.frontend.ast.AstBooleanConstantExpression;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstExpressionStatement;
import org.sirius.frontend.ast.AstFloatConstantExpression;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstIntegerConstantExpression;
import org.sirius.frontend.ast.AstMemberAccessExpression;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStringConstantExpression;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.ConstructorCallExpression;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.FunctionCallExpressionContext;
import org.sirius.frontend.parser.SiriusParser.IsMethodCallExpressionContext;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.equalTo;

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
		
		StatementParser.ReturnStatementVisitor visitor = new StatementParser.ReturnStatementVisitor(reporter);
		AstReturnStatement stmt = visitor.visit(tree);

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
		
		StatementParser.ExpressionStatementVisitor visitor = new StatementParser.ExpressionStatementVisitor(reporter);
		AstExpressionStatement stmt = visitor.visit(tree);

		assertThat(stmt, instanceOf(AstExpressionStatement.class) );
		AstExpressionStatement e = ((AstExpressionStatement)stmt);
		verify.accept(e);
		return e;
	}

}
