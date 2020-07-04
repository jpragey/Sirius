package org.sirius.frontend.core.parser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassOrInterface;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionFormalArgumentContext;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ReturnStatementContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationContext;

import com.google.common.collect.ImmutableList;

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

	public static class ReturnStatementVisitor extends SiriusBaseVisitor<AstReturnStatement> {
		private Reporter reporter;
		
		public ReturnStatementVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstReturnStatement visitReturnStatement(ReturnStatementContext ctx) {
			ExpressionParser.ExpressionVisitor visitor = new ExpressionParser.ExpressionVisitor(reporter);
			AstExpression returnStatement = ctx.expression.accept(visitor);
			
			return new AstReturnStatement(returnStatement);
		}
		
	}

}
