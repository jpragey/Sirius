package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstBinaryOpExpression;
import org.sirius.frontend.ast.AstBooleanConstantExpression;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstFloatConstantExpression;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstIntegerConstantExpression;
import org.sirius.frontend.ast.AstMemberAccessExpression;
import org.sirius.frontend.ast.AstStringConstantExpression;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ConstructorCallExpression;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.core.parser.LambdaDeclarationParser.LambdaDefinitionVisitor;
import org.sirius.frontend.parser.SParserBaseVisitor;
import org.sirius.frontend.parser.SParser.ClassInstanciationExpressionContext;
import org.sirius.frontend.parser.SParser.ConstantExpressionContext;
import org.sirius.frontend.parser.SParser.ExpressionContext;
import org.sirius.frontend.parser.SParser.FunctionCallExpressionContext;
import org.sirius.frontend.parser.SParser.IsBinaryExpressionContext;
import org.sirius.frontend.parser.SParser.IsConstructorCallExpressionContext;
import org.sirius.frontend.parser.SParser.IsFieldAccessExpressionContext;
import org.sirius.frontend.parser.SParser.IsMethodCallExpressionContext;
import org.sirius.frontend.parser.SParser.IsVariableRefExpressionContext;
import org.sirius.frontend.parser.SParser.LambdaDefinitionContext;
import org.sirius.frontend.symbols.Scope;

/**
 * Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ExpressionParser {
	private Reporter reporter;
	private LambdaDeclarationParser lambdaDeclarationParser;
	private CommonTokenStream tokens;

	public ExpressionParser(Reporter reporter, CommonTokenStream tokens) {
		super();
		this.reporter = reporter;
		this.lambdaDeclarationParser = new LambdaDeclarationParser(reporter, tokens);
		this.tokens = tokens;
	}

	public class ExpressionVisitor extends SParserBaseVisitor<AstExpression> {

		public ExpressionVisitor() {
			super();
		}

		@Override
		public AstExpression visitIsBinaryExpression(IsBinaryExpressionContext ctx) {
			AstExpression left = visit(ctx.left);
			AstExpression right = visit(ctx.right);
			AstToken opToken = new AstToken(ctx.op);

			return new AstBinaryOpExpression(left, right, opToken);
		}

		@Override
		public AstExpression visitConstantExpression(ConstantExpressionContext ctx) {

			TerminalNode stringNode = ctx.STRING();
			TerminalNode booleanNode = ctx.BOOLEAN();
			TerminalNode integerNode = ctx.INTEGER();
			TerminalNode floatNode = ctx.FLOAT();

			if (stringNode != null)
				return new AstStringConstantExpression(new AstToken(stringNode.getSymbol()));

			if (integerNode != null)
				return new AstIntegerConstantExpression(new AstToken(integerNode.getSymbol()), reporter);

			if (booleanNode != null)
				return new AstBooleanConstantExpression(new AstToken(booleanNode.getSymbol()));

			if (floatNode != null)
				return new AstFloatConstantExpression(new AstToken(floatNode.getSymbol()));

			reporter.fatal("Unsupported constant expression type...", new AstToken(ctx.start));
			AstExpression dummyExpression = new AstIntegerConstantExpression(AstToken.internal("0"), reporter);
			return dummyExpression;
		}

		@Override
		public AstFunctionCallExpression visitFunctionCallExpression(FunctionCallExpressionContext ctx) {
			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());

			ExpressionVisitor argVisitor = new ExpressionVisitor();

			List<AstExpression> actualArguments = ctx.children.stream().map(tree -> tree.accept(argVisitor))
					.filter(tree -> tree != null).collect(Collectors.toList());

			Optional<AstExpression> thisExpression = Optional.empty(); /* TODO : ???? */

			return new AstFunctionCallExpression(reporter, name, actualArguments, thisExpression);
		}

		@Override
		public AstExpression visitIsMethodCallExpression(IsMethodCallExpressionContext ctx) {

			ExpressionVisitor argVisitor = new ExpressionVisitor();
			AstExpression thisExpr = argVisitor.visit(ctx.thisExpr);
			assert (thisExpr != null); // TODO: implements all this-expressions...

			AstFunctionCallExpression fctCallExpr = visitFunctionCallExpression(ctx.functionCallExpression());

			fctCallExpr.setThisExpression(thisExpr);

			return fctCallExpr;
		}

		@Override
		public AstExpression visitClassInstanciationExpression(ClassInstanciationExpressionContext ctx) {
			AstToken name = new AstToken(ctx.name);

			ExpressionVisitor argVisitor = new ExpressionVisitor();

			List<AstExpression> actualArguments = ctx.children.stream()
//					.map(tree -> tree.accept(argVisitor))
					.map(tree -> argVisitor.visit(tree)/* tree.accept(argVisitor) */).filter(express -> express != null)
					.collect(Collectors.toList());

			return new ConstructorCallExpression(reporter, name, actualArguments);
		}

		@Override
		public AstExpression visitIsFieldAccessExpression(IsFieldAccessExpressionContext ctx) {

			AstExpression thisExpression = visit(ctx.lhs);
			AstToken valueName = new AstToken(ctx.LOWER_ID().getSymbol());

			return new AstMemberAccessExpression(reporter, thisExpression, valueName);
		}

		@Override
		public SimpleReferenceExpression visitIsVariableRefExpression(IsVariableRefExpressionContext ctx) {
			AstToken refName = new AstToken(ctx.ref);

			return new SimpleReferenceExpression(reporter, refName);
		}

		@Override
		public AstExpression visitLambdaDefinition(LambdaDefinitionContext ctx) {
			LambdaDefinitionVisitor v = lambdaDeclarationParser.new LambdaDefinitionVisitor();
			LambdaDefinition ld = v.visit(ctx);

			return ld;
		}

	}

}
