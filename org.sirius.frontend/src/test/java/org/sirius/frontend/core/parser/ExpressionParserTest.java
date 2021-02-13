package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.BinaryOpExpression;
import org.sirius.frontend.ast.AstBinaryOpExpression;
import org.sirius.frontend.ast.AstBooleanConstantExpression;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstFloatConstantExpression;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstIntegerConstantExpression;
import org.sirius.frontend.ast.AstMemberAccessExpression;
import org.sirius.frontend.ast.AstStringConstantExpression;
import org.sirius.frontend.ast.ConstructorCallExpression;
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.FunctionCallExpressionContext;

public class ExpressionParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstExpression parseExpression(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
//		ParseTree tree = parser.constantExpression();
		ParseTree tree = parser.expression();
				
		ExpressionParser.ExpressionVisitor typeVisitor = new ExpressionParser.ExpressionVisitor(reporter);
		AstExpression expression = typeVisitor.visit(tree);
		return expression;
	}
	
	@Test
	@DisplayName("Integer constant expressions")
	public void integerConstant() {
		assertEquals(parseIntegerConstant("666"), 666);
		assertEquals(parseIntegerConstant("0"), 0);
		assertEquals(parseIntegerConstant("-1"), -1);
		assertEquals(parseIntegerConstant("+1"), +1);
	}

	private int parseIntegerConstant(String inputText) {
		AstExpression expression = parseExpression(inputText);
		assertThat(expression, instanceOf(AstIntegerConstantExpression.class) );
		int value = ((AstIntegerConstantExpression)expression).getValue();
		return value;
	}

	@Test
	@DisplayName("String constant expressions")
	public void stringConstant() {
		assertEquals(parseStringConstant("\"abc\""), "abc");
		assertEquals(parseStringConstant("\"\""), "");
	}

	private String parseStringConstant(String inputText) {
		AstExpression expression = parseExpression(inputText);
		assertThat(expression, instanceOf(AstStringConstantExpression.class) );
		String value = ((AstStringConstantExpression)expression).getContentString();
		return value;
	}

	@Test
	@DisplayName("Boolean constant expressions")
	public void booleanConstant() {
		assertEquals(parseBooleanConstant("true"), true);
		assertEquals(parseBooleanConstant("false"), false);
	}

	private boolean parseBooleanConstant(String inputText) {
		AstExpression expression = parseExpression(inputText);
		assertThat(expression, instanceOf(AstBooleanConstantExpression.class) );
		boolean value = ((AstBooleanConstantExpression)expression).getValue();
		return value;
	}

	@Test
	@DisplayName("Float constant expressions")
	public void floatConstant() {
		assertEquals(parseDoubleConstant("1.2"), 1.2);
	}

	private double parseDoubleConstant(String inputText) {
		AstExpression expression = parseExpression(inputText);
		assertThat(expression, instanceOf(AstFloatConstantExpression.class) );
		double value = ((AstFloatConstantExpression)expression).getValue();
		return value;
	}

	@Test
	@DisplayName("Binary op expressions")
	public void binaryOpExpression() {
		checkBinaryOpExpression("1 ^ 2", "1", "2", BinaryOpExpression.Operator.Exponential);

		checkBinaryOpExpression("1 * 2", "1", "2", BinaryOpExpression.Operator.Mult);
		checkBinaryOpExpression("1 / 2", "1", "2", BinaryOpExpression.Operator.Divide);

		checkBinaryOpExpression("1 + 2", "1", "2", BinaryOpExpression.Operator.Add);
		checkBinaryOpExpression("1 - 2", "1", "2", BinaryOpExpression.Operator.Substract);

		checkBinaryOpExpression("1 > 2", "1", "2", BinaryOpExpression.Operator.Greater);
		checkBinaryOpExpression("1 >= 2", "1", "2", BinaryOpExpression.Operator.GreaterOrEqual);
		checkBinaryOpExpression("1 < 2", "1", "2", BinaryOpExpression.Operator.Lower);
		checkBinaryOpExpression("1 <= 2", "1", "2", BinaryOpExpression.Operator.LowerOrEqual);

		checkBinaryOpExpression("1 == 2", "1", "2", BinaryOpExpression.Operator.EqualEqual);
		checkBinaryOpExpression("1 != 2", "1", "2", BinaryOpExpression.Operator.NotEqual);

		checkBinaryOpExpression("true && true", "true", "true", BinaryOpExpression.Operator.AndAnd);
		checkBinaryOpExpression("true || true", "true", "true", BinaryOpExpression.Operator.OrOr);

		checkBinaryOpExpression("1 = 2", "1", "2", BinaryOpExpression.Operator.Equal);
		checkBinaryOpExpression("1 += 2", "1", "2", BinaryOpExpression.Operator.PlusEqual);
		checkBinaryOpExpression("1 -= 2", "1", "2", BinaryOpExpression.Operator.MinusEqual);
		checkBinaryOpExpression("1 *= 2", "1", "2", BinaryOpExpression.Operator.MultEqual);
		checkBinaryOpExpression("1 /= 2", "1", "2", BinaryOpExpression.Operator.DivideEqual);
	}

	private AstExpression parseBinaryExpression(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.expression();
				
		ExpressionParser.ExpressionVisitor expressionVisitor = new ExpressionParser.ExpressionVisitor(reporter);
		AstExpression expression = expressionVisitor.visit(tree);
		return expression;
	}

	private void checkBinaryOpExpression(String inputText, String expectedLeft, String expectedRight, BinaryOpExpression.Operator expectedOp) {
		AstExpression expression = parseBinaryExpression(inputText);
		
		assertThat(expression, instanceOf(AstBinaryOpExpression.class) );
		AstBinaryOpExpression e = ((AstBinaryOpExpression)expression);

		assertEquals(e.getRight().getExpression().get().toString(), expectedRight);
		assertEquals(e.getLeft() .getExpression().get().toString(), expectedLeft);
		assertEquals(e.getOperator(), expectedOp);
	}

	@Test
	@DisplayName("Function call expressions")
	public void functioCallExpression() {
		checkFunctionCallExpression("f()",      "f", (e -> {assertEquals(e.getActualArguments().size(), 0);}) );
		checkFunctionCallExpression("f(1)",     "f", (e -> {assertEquals(e.getActualArguments().size(), 1);}) );
		checkFunctionCallExpression("f(1,2,3)", "f", (e -> {assertEquals(e.getActualArguments().size(), 3);}) );
	}

	private AstFunctionCallExpression parseFunctioCallExpression(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		FunctionCallExpressionContext tree = parser.functionCallExpression();
				
		ExpressionParser.ExpressionVisitor expressionVisitor = new ExpressionParser.ExpressionVisitor(reporter);
		
		AstExpression expression = expressionVisitor.visitFunctionCallExpression(tree);

		assertThat(expression, instanceOf(AstFunctionCallExpression.class) );	// ???
		AstFunctionCallExpression e = ((AstFunctionCallExpression)expression);
		return e;
	}

	private AstFunctionCallExpression checkFunctionCallExpression(String inputText, String expectedName, 
			Consumer<AstFunctionCallExpression> verify) 
	{
		AstFunctionCallExpression expression = parseFunctioCallExpression(inputText);
		verify.accept(expression);
		return expression;
	}

	@Test
	@DisplayName("Method call expressions")
	public void methodCallExpression() {
		checkMethodCallExpression("1.f()",      "f", (e -> {assertEquals(e.getActualArguments().size(), 0);}) );
		checkMethodCallExpression("1.f(1)",     "f", (e -> {assertEquals(e.getActualArguments().size(), 1);}) );
		checkMethodCallExpression("1.f(1,2,3)", "f", (e -> {assertEquals(e.getActualArguments().size(), 3);}) );
	}

	private AstFunctionCallExpression parseMethodCallExpression(String inputText) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.expression();
		
		ExpressionParser.ExpressionVisitor typeVisitor = new ExpressionParser.ExpressionVisitor(reporter);
		AstExpression expression = typeVisitor.visit(tree);

		assertThat(expression, instanceOf(AstFunctionCallExpression.class) );	// ???
		AstFunctionCallExpression e = ((AstFunctionCallExpression)expression);
		return e;
	}

	private AstFunctionCallExpression checkMethodCallExpression(String inputText, String expectedName, 
			Consumer<AstFunctionCallExpression> verify) 
	{
		AstFunctionCallExpression expression = parseMethodCallExpression(inputText);
		verify.accept(expression);
		return expression;
	}

	@Test
	@DisplayName("Constructor call expressions")
	public void constructorCallExpression() {
		parseConstructorCallExpression("F()",      (e -> {assertEquals(e.getActualArguments().size(), 0);}) );
		parseConstructorCallExpression("F(1)",     (e -> {assertEquals(e.getActualArguments().size(), 1);}) );
		parseConstructorCallExpression("F(1,2,3)", (e -> {assertEquals(e.getActualArguments().size(), 3);}) );
	}

	private ConstructorCallExpression parseConstructorCallExpression(String inputText, Consumer<ConstructorCallExpression> verify) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.expression();
		
		ExpressionParser.ExpressionVisitor typeVisitor = new ExpressionParser.ExpressionVisitor(reporter);
		AstExpression expression = typeVisitor.visit(tree);

		assertThat(expression, instanceOf(ConstructorCallExpression.class) );	// ???
		ConstructorCallExpression e = ((ConstructorCallExpression)expression);
		verify.accept(e);
		return e;
	}

	@Test
	@DisplayName("Member access (<expr>.field) expressions")
	public void memberAccessExpression() {
		parseMemberAccessExpression("1.aa",      ((AstMemberAccessExpression e) -> {assertThat(e.getValueName().getText(), equalTo("aa") );}) );
	}

	private AstMemberAccessExpression parseMemberAccessExpression(String inputText, Consumer<AstMemberAccessExpression> verify) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.expression();
		
		ExpressionParser.ExpressionVisitor typeVisitor = new ExpressionParser.ExpressionVisitor(reporter);
		AstExpression expression = typeVisitor.visit(tree);

		assertThat(expression, instanceOf(AstMemberAccessExpression.class) );
		AstMemberAccessExpression e = ((AstMemberAccessExpression)expression);
		verify.accept(e);
		return e;
	}

	@Test
	@DisplayName("Single-id reference (Local/member/global variable, function parameter)")
	public void variableRefExpression() {
		parseSimpleReferenceExpression("aa",      ((SimpleReferenceExpression e) -> {assertThat(e.getNameString(), equalTo("aa") );}) );
	}

	private SimpleReferenceExpression parseSimpleReferenceExpression(String inputText, Consumer<SimpleReferenceExpression> verify) {
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.expression();
		
		ExpressionParser.ExpressionVisitor typeVisitor = new ExpressionParser.ExpressionVisitor(reporter);
		AstExpression expression = typeVisitor.visit(tree);

		assertThat(expression, instanceOf(SimpleReferenceExpression.class) );
		SimpleReferenceExpression e = ((SimpleReferenceExpression)expression);
		verify.accept(e);
		return e;
	}

}
