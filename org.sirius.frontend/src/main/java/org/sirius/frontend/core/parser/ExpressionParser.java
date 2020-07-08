package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ConstantExpressionContext;
import org.sirius.frontend.parser.SiriusParser.ExpressionContext;
import org.sirius.frontend.parser.SiriusParser.FunctionCallExpressionContext;
import org.sirius.frontend.parser.SiriusParser.IsBinaryExpressionContext;
import org.sirius.frontend.parser.SiriusParser.IsConstructorCallExpressionContext;
import org.sirius.frontend.parser.SiriusParser.IsFieldAccessExpressionContext;
import org.sirius.frontend.parser.SiriusParser.IsMethodCallExpressionContext;
import org.sirius.frontend.parser.SiriusParser.IsVariableRefExpressionContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ExpressionParser {
	private Reporter reporter;
	
	public ExpressionParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class ExpressionVisitor extends SiriusBaseVisitor<AstExpression> {
		private Reporter reporter;
		
		public ExpressionVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		
		

		@Override
		public AstExpression visitIsBinaryExpression(IsBinaryExpressionContext ctx) {
			AstExpression left = ctx.left.accept(this);
			AstExpression right = ctx.right.accept(this);
			AstToken opToken = new AstToken(ctx.op);
			
			return new AstBinaryOpExpression(left, right, opToken);
		}



		@Override
		public AstExpression visitConstantExpression(ConstantExpressionContext ctx) {
			
			TerminalNode stringNode = ctx.STRING();
			TerminalNode booleanNode = ctx.BOOLEAN();
			TerminalNode integerNode = ctx.INTEGER();
			TerminalNode floatNode = ctx.FLOAT();
			
			if(stringNode != null)
				return new AstStringConstantExpression(new AstToken(stringNode.getSymbol()));
			
			if(integerNode != null)
				return new AstIntegerConstantExpression(new AstToken(integerNode.getSymbol()), reporter);
			
			if(booleanNode != null)
				return new AstBooleanConstantExpression(new AstToken(booleanNode.getSymbol()));
			
			if(floatNode != null)
				return new AstFloatConstantExpression(new AstToken(floatNode.getSymbol()));
			
			reporter.fatal("Unsupported constant expression type...", new AstToken(ctx.start));
			AstExpression dummyExpression = new AstIntegerConstantExpression(AstToken.internal("0"), reporter);
			return dummyExpression;
		}

		@Override
		public AstFunctionCallExpression visitFunctionCallExpression(FunctionCallExpressionContext ctx) {
			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());
			
			ExpressionVisitor argVisitor = new ExpressionVisitor(reporter);
			
			List<AstExpression> actualArguments = ctx.children.stream()
					.map(tree -> tree.accept(argVisitor))
					.filter(tree -> tree!=null)
					.collect(Collectors.toList());

			Optional<AstExpression> thisExpression = Optional.empty();
			
			return new AstFunctionCallExpression(reporter, name, actualArguments, thisExpression );
		}



		@Override
		public AstExpression visitIsMethodCallExpression(IsMethodCallExpressionContext ctx) {

			ExpressionVisitor argVisitor = new ExpressionVisitor(reporter);
			ExpressionContext thisExprContext = ctx.thisExpr;
			AstExpression thisExpr = thisExprContext.accept(argVisitor);
			assert(thisExpr != null); // TODO: implements all this-expressions...

			AstFunctionCallExpression fctCallExpr = visitFunctionCallExpression(ctx.functionCallExpression());
			
			fctCallExpr.setThisExpression(thisExpr);
			
			return fctCallExpr;
		}



		@Override
		public ConstructorCallExpression visitIsConstructorCallExpression(IsConstructorCallExpressionContext ctx) {
			AstToken name = new AstToken(ctx.name);
			
			ExpressionVisitor argVisitor = new ExpressionVisitor(reporter);
			
			List<AstExpression> actualArguments = ctx.children.stream()
					.map(tree -> tree.accept(argVisitor))
					.filter(tree -> tree!=null)
					.collect(Collectors.toList());

			return new ConstructorCallExpression(reporter, name, actualArguments);
		}



		@Override
		public AstExpression visitIsFieldAccessExpression(IsFieldAccessExpressionContext ctx) {
			
			AstExpression thisExpression = ctx.lhs.accept(this /*Osé*/ );
			AstToken valueName = new AstToken(ctx.LOWER_ID().getSymbol());
			
			return new AstMemberAccessExpression(reporter, thisExpression, valueName);
		}



		@Override
		public SimpleReferenceExpression visitIsVariableRefExpression(IsVariableRefExpressionContext ctx) {
			AstToken refName = new AstToken(ctx.ref);

			return new SimpleReferenceExpression(reporter, refName);
		}
		
		
		
	}

}
