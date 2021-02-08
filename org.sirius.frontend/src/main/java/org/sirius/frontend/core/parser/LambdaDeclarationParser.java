package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.FunctionBody;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.core.parser.FunctionDeclarationParser.FunctionParameterVisitor;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.FunctionBodyContext;
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

	//AstFunctionParameter
/***	
	public static class LambdaParameterVisitor extends SiriusBaseVisitor<AstLambdaParameter> {
		private Reporter reporter;
		
		public LambdaParameterVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstLambdaParameter visitLambdaDeclaration(LambdaDeclarationContext ctx) {
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			AstType type = typeVisitor.visit(ctx.type());

			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());

			AstLambdaParameter lp = new AstLambdaParameter(type);
			
			return super.visitLambdaDeclaration(ctx);
		}
//		@Override
//		public AstLambdaParameter visitFunctionFormalArgument(FunctionFormalArgumentContext ctx) {
//			
//			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
//			
//			AstType type = typeVisitor.visit(ctx.type());
//			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());
//
//			return new AstFunctionParameter(type, name);
//		}
		
	}
*/
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

	public static class LambdaDeclarationVisitor extends SiriusBaseVisitor<LambdaDefinition> {
		private Reporter reporter;

		public LambdaDeclarationVisitor(Reporter reporter/*, QName containerQName*/) {
			super();
			this.reporter = reporter;
		}

		@Override
		public LambdaDefinition visitLambdaDefinition(LambdaDefinitionContext ctx) {
			
			// -- Function parameters
			
			FunctionParameterVisitor paramVisitor = new FunctionParameterVisitor(reporter);
			List<AstFunctionParameter> functionParams = ctx.lambdaFormalArgument().stream()
					.map(funcParam -> paramVisitor.visitLambdaFormalArgument(funcParam))
					.collect(Collectors.toList());
			int currentArgIndex = 0; // index in argument list
			for(var fp: functionParams) {
				fp.setIndex(currentArgIndex++);
			}
			
//			List<AstFunctionParameter> functionParams = Collections.emptyList();// TODO: temp
			
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
