package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.FunctionBody;
import org.sirius.frontend.ast.LambdaClosure;
import org.sirius.frontend.ast.LambdaDeclaration;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.FunctionBodyContext;
import org.sirius.frontend.parser.SiriusParser.LambdaDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.LambdaDefinitionContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class LambdaDeclarationParser {
	private Reporter reporter;
	
	public LambdaDeclarationParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class FunctionBodyVisitor extends SiriusBaseVisitor<List<AstStatement> > {
		private Reporter reporter;
		
		public FunctionBodyVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public List<AstStatement> visitFunctionBody(FunctionBodyContext ctx) {
			StatementParser.StatementVisitor statementVisitor = new StatementParser.StatementVisitor(reporter);
			
			List<AstStatement> statements =  ctx.statement().stream()
				.map(stmtCtxt -> stmtCtxt.accept(statementVisitor))
				.collect(Collectors.toList());
			
			return statements;
		}
	}

	public static class LambdaDeclarationVisitor extends SiriusBaseVisitor<LambdaDeclaration> {
		private Reporter reporter;

		public LambdaDeclarationVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		
		@Override
		public LambdaDeclaration visitLambdaDeclaration(LambdaDeclarationContext ctx) {
			LambdaClosure closure = new LambdaClosure(); // TODO, 
			
			TypeParser.TypeVisitor argTypeVisitor = new TypeParser.TypeVisitor(reporter);

			List<AstType> args = ctx.functionDeclarationParameterList().functionDeclarationParameter().stream()
					.map(typeContext -> argTypeVisitor.visit(typeContext))
					.collect(Collectors.toList());
			
			TypeParser.TypeVisitor returnTypeVisitor = new TypeParser.TypeVisitor(reporter);
			AstType returnType = returnTypeVisitor.visit(ctx.returnType);
			
			LambdaDeclaration ld = new LambdaDeclaration(closure, args, returnType);
			return ld;
		}
	}

	public class LambdaDefinitionVisitor extends SiriusBaseVisitor<LambdaDefinition> {
//		private Reporter reporter;
		private Parsers parsers;

		public LambdaDefinitionVisitor(/*Reporter reporter*/) {
			super();
//			this.reporter = reporter;
			this.parsers = new Parsers(reporter);
		}
		
		@Override
		public LambdaDefinition visitLambdaDefinition(LambdaDefinitionContext ctx) {
			
			// -- Function parameters
			Parsers.FunctionParameterListVisitor argListVisitor = parsers.new FunctionParameterListVisitor();
			
			List<AstFunctionParameter> functionParams = argListVisitor.visit(ctx.functionDefinitionParameterList());
			
			int currentArgIndex = 0; // index in argument list
			for(var fp: functionParams) {
				fp.setIndex(currentArgIndex++);
			}
			
			// -- Return type
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			TypeContext returnContext = ctx.returnType;
			AstType returnType =  (returnContext == null) ?
				AstVoidType.instance :
				typeVisitor.visit(returnContext);
			
			// -- Body
			FunctionBodyVisitor bodyVisitor = new FunctionBodyVisitor(reporter);
			List<AstStatement> statements = ctx.functionBody().accept(bodyVisitor);
			
			return new LambdaDefinition(functionParams, returnType, new FunctionBody(statements));
		}
	}

}
