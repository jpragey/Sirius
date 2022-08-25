package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
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
import org.sirius.frontend.parser.Sirius.FunctionBodyContext;
import org.sirius.frontend.parser.Sirius.LambdaDeclarationContext;
import org.sirius.frontend.parser.Sirius.LambdaDefinitionContext;
import org.sirius.frontend.parser.Sirius.TypeContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class LambdaDeclarationParser {
	private Reporter reporter;
	private CommonTokenStream tokens;
	
	public LambdaDeclarationParser(Reporter reporter, CommonTokenStream tokens) {
		super();
		this.reporter = reporter;
		this.tokens = tokens;
	}

	public class FunctionBodyVisitor extends SiriusBaseVisitor<List<AstStatement> > {
		private Reporter reporter;
		
		public FunctionBodyVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public List<AstStatement> visitFunctionBody(FunctionBodyContext ctx) {
			StatementParser.StatementVisitor statementVisitor = new StatementParser(reporter, tokens).new StatementVisitor();
			
			List<AstStatement> statements =  ctx.statement().stream()
				.map(statementVisitor::visit /*stmtCtxt -> stmtCtxt.accept(statementVisitor)*/)
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
					.map(argTypeVisitor::visit)
//					.map(typeContext -> argTypeVisitor.visit(typeContext))
					.collect(Collectors.toList());
			
			TypeParser.TypeVisitor returnTypeVisitor = new TypeParser.TypeVisitor(reporter);
			AstType returnType = returnTypeVisitor.visit(ctx.returnType);
			
			LambdaDeclaration ld = new LambdaDeclaration(closure, args, returnType);
			return ld;
		}
	}

	public class LambdaDefinitionVisitor extends SiriusBaseVisitor<LambdaDefinition> {
		private Parsers parsers;

		public LambdaDefinitionVisitor() {
			super();
			this.parsers = new Parsers(reporter, tokens);
		}
		
		@Override
		public LambdaDefinition visitLambdaDefinition(LambdaDefinitionContext ctx) {
			
			// -- Function parameters
			Parsers.FunctionParameterListVisitor argListVisitor = parsers.new FunctionParameterListVisitor();
			
			List<AstFunctionParameter> functionParams = argListVisitor.visit(ctx.functionDefinitionParameterList());
			
			// assign index in argument list
			int currentArgIndex = 0; 
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
//			List<AstStatement> statements = ctx.functionBody().accept(bodyVisitor);
			List<AstStatement> statements = bodyVisitor.visit(ctx);
			
			return new LambdaDefinition(functionParams, returnType, new FunctionBody(statements));
		}
	}

}
