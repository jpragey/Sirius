package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstBlock;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstExpressionStatement;
import org.sirius.frontend.ast.AstIfElseStatement;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.parser.SParserBaseVisitor;
import org.sirius.frontend.parser.SParser.BlockStatementContext;
import org.sirius.frontend.parser.SParser.ExpressionContext;
import org.sirius.frontend.parser.SParser.IfElseStatementContext;
import org.sirius.frontend.parser.SParser.IsExpressionStatementContext;
import org.sirius.frontend.parser.SParser.LocalVariableStatementContext;
import org.sirius.frontend.parser.SParser.ReturnStatementContext;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;


/** Visitor-based parser for the 'statement' rule.
 *
 * 
 * statement returns [AstStatement stmt]
	: returnStatement			# isReturnStatement
	| expression ';'			# isExpressionStatement
	| localVariableStatement	# isLocalVaribleStatement
	| ifElseStatement			# isIfElseStatement
	| blockStatement			# isBlockStatement
	;

 * @author jpragey
 *
 */
public class StatementParser {
	private Reporter reporter;
	private CommonTokenStream tokens;
	
	public StatementParser(Reporter reporter, CommonTokenStream tokens) {
		super();
		this.reporter = reporter;
		this.tokens = tokens;
	}

	public class StatementVisitor extends SParserBaseVisitor<AstStatement> {
		
		public StatementVisitor() {
			super();
		}

		@Override
		public AstReturnStatement visitReturnStatement(ReturnStatementContext ctx) {
			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser(reporter, tokens).new ExpressionVisitor();
			AstExpression returnStatement = visitor.visit(ctx.expression());
			
			return new AstReturnStatement(returnStatement);
		}
		
		@Override
		public AstExpressionStatement visitIsExpressionStatement(IsExpressionStatementContext ctx) {
			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser(reporter, tokens).new ExpressionVisitor();
			AstExpression expression = visitor.visit(ctx.expression());
			
			return new AstExpressionStatement(expression);
		}

		@Override
		public AstLocalVariableStatement visitLocalVariableStatement(LocalVariableStatementContext ctx) {
			AnnotationList annotationList = new AnnotationList();	// TODO
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			AstType type = typeVisitor.visit(ctx.type());
			AstToken varName = new AstToken(ctx.LOWER_ID().getSymbol());

			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser(reporter, tokens).new ExpressionVisitor();
			
			ExpressionContext exprCtxt = ctx.expression();
			Optional<AstExpression> initExpression = Optional.ofNullable(exprCtxt).map(visitor::visit);
			
			return new AstLocalVariableStatement(annotationList, type, varName, initExpression);
		}
		
		@Override
		public AstIfElseStatement visitIfElseStatement(IfElseStatementContext ctx) {

			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser(reporter, tokens).new ExpressionVisitor();
			AstExpression ifExpression = ctx.ifExpression.accept(visitor);
			
			StatementParser.StatementVisitor stmtVisitor = new StatementParser(reporter, tokens).new StatementVisitor();
			AstStatement ifBlock = ctx.ifBlock.accept(stmtVisitor);
			
			Optional<AstStatement> elseBlock = (ctx.elseBlock != null) ? 
					Optional.of(stmtVisitor.visit(ctx.elseBlock)) : 
					Optional.empty();
			
			return new AstIfElseStatement(reporter, ifExpression, ifBlock, elseBlock);
		}
		@Override
		public AstStatement visitBlockStatement(BlockStatementContext ctx) {
			StatementParser.StatementVisitor statementVisitor = new StatementParser(reporter, tokens).new StatementVisitor();
			
			List<AstStatement> statements =  ctx.statement().stream()
				.map(statementVisitor::visit)
				.collect(Collectors.toList());

			return new AstBlock(statements);
		}
	}

}
