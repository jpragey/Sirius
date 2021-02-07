package org.sirius.frontend.core.parser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstLambdaParameter;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.LambdaDeclaration;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.FunctionBodyContext;
import org.sirius.frontend.parser.SiriusParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionFormalArgumentContext;
import org.sirius.frontend.parser.SiriusParser.LambdaDeclarationContext;
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

	public static class LambdaDeclarationVisitor extends SiriusBaseVisitor<LambdaDeclaration> {
		private Reporter reporter;

		public LambdaDeclarationVisitor(Reporter reporter/*, QName containerQName*/) {
			super();
			this.reporter = reporter;
		}

		@Override
		public LambdaDeclaration visitLambdaDeclaration(LambdaDeclarationContext ctx) {
			// TODO Auto-generated method stub
//			return super.visitLambdaDeclaration(ctx);
//			return new LambdaDeclaration();
//		}
		
//		//@Override
//		public PartialList visitFunctionDeclaration_OLD(FunctionDeclarationContext ctx) {
//			
//			AstToken name = new AstToken(ctx.name);
//			QName qName = containerQName.child(name.getText());
			
			// -- Function parameters
/**			
			FunctionParameterVisitor paramVisitor = new FunctionParameterVisitor(reporter);
			List<AstFunctionParameter> functionParams = ctx.functionFormalArgument().stream()
					.map(funcParam -> paramVisitor.visitFunctionFormalArgument(funcParam))
					.collect(Collectors.toList());
			int currentArgIndex = 0; // index in argument list
			for(var fp: functionParams) {
				fp.setIndex(currentArgIndex++);
			}
*/			
			List<AstFunctionParameter> functionParams = Collections.emptyList();// TODO: temp
			
			// -- Return type
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			TypeContext returnContext = ctx.returnType;
			AstType returnType =  (returnContext == null) ?
				AstVoidType.instance :
				typeVisitor.visit(returnContext);
			
			// -- Body
			
			Optional<List<AstStatement>> body = Optional.empty();
/***			
			if(ctx.functionBody() != null) {
				FunctionBodyVisitor bodyVisitor = new FunctionBodyVisitor(reporter);
				List<AstStatement> statements = ctx.functionBody().accept(bodyVisitor);
				body = Optional.of(statements);
			}
			
//			boolean member = false; 
*/
			return new LambdaDeclaration(functionParams, returnType, body);

//			return new PartialList(functionParams, returnType, member, /* qName,*/ /*concrete, */name, body) ;
		}
	}

}
