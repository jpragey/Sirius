package org.sirius.frontend.core.parser;

import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstExpressionStatement;
import org.sirius.frontend.ast.AstIfElseStatement;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ExpressionContext;
import org.sirius.frontend.parser.SiriusParser.IfElseStatementContext;
import org.sirius.frontend.parser.SiriusParser.IsExpressionStatementContext;
import org.sirius.frontend.parser.SiriusParser.LocalVariableStatementContext;
import org.sirius.frontend.parser.SiriusParser.ReturnStatementContext;


/** Visitor-based parser for the 'statement' rule.
 *
 * 
 * statement returns [AstStatement stmt]
	: returnStatement	{ $stmt = $returnStatement.stmt; }								# isReturnStatement
	| expression ';'	{ $stmt = new AstExpressionStatement($expression.express); }	# isExpressionStatement
	| localVariableStatement	{ $stmt = $localVariableStatement.lvStatement; }		# isLocalVaribleStatement
	| ifElseStatement	{ $stmt = $ifElseStatement.stmt; }								# isIfElseStatement
	;

 * @author jpragey
 *
 */
public class StatementParser {
	private Reporter reporter;
	
	public StatementParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class StatementVisitor extends SiriusBaseVisitor<AstStatement> {
//		public static class ReturnStatementVisitor extends SiriusBaseVisitor<AstReturnStatement> {
		private Reporter reporter;
		
		public StatementVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstReturnStatement visitReturnStatement(ReturnStatementContext ctx) {
			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser.ExpressionVisitor(reporter);
			AstExpression returnStatement = ctx.expression().accept(visitor);
			
			return new AstReturnStatement(returnStatement);
		}
		
		@Override
		public AstExpressionStatement visitIsExpressionStatement(IsExpressionStatementContext ctx) {
			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser.ExpressionVisitor(reporter);
			AstExpression expression = ctx.expression().accept(visitor);
			
			return new AstExpressionStatement(expression);
		}

		@Override
		public AstLocalVariableStatement visitLocalVariableStatement(LocalVariableStatementContext ctx) {
			AnnotationList annotationList = new AnnotationList();	// TODO
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			AstType type = ctx.type().accept(typeVisitor);
			AstToken varName = new AstToken(ctx.LOWER_ID().getSymbol());

			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser.ExpressionVisitor(reporter);
			
			Optional<AstExpression> initExpression = Optional.empty();
			ExpressionContext exprCtxt = ctx.expression();
			if(exprCtxt != null) {
				initExpression = Optional.of(exprCtxt.accept(visitor));
			}
			
			return new AstLocalVariableStatement(annotationList, type, varName, initExpression);
		}
		
		@Override
		public AstIfElseStatement visitIfElseStatement(IfElseStatementContext ctx) {

			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser.ExpressionVisitor(reporter);
			AstExpression ifExpression = ctx.ifExpression.accept(visitor);
			
			StatementParser.StatementVisitor stmtVisitor = new StatementParser.StatementVisitor(reporter);
			AstStatement ifBlock = ctx.ifBlock.accept(stmtVisitor);
			
			Optional<AstStatement> elseBlock = (ctx.elseBlock != null) ? 
					Optional.of(ctx.elseBlock.accept(stmtVisitor)) : 
					Optional.empty();
			
			return new AstIfElseStatement(reporter, ifExpression, ifBlock, elseBlock);
		}
	}

}
