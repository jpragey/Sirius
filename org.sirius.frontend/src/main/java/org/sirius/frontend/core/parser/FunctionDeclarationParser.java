package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.parser.SParser.FunctionBodyContext;
import org.sirius.frontend.parser.SParser.FunctionDefinitionContext;
import org.sirius.frontend.parser.SParser.FunctionDefinitionParameterContext;
import org.sirius.frontend.parser.SParser.TypeContext;
import org.sirius.frontend.parser.SParser.TypeParameterDeclarationListContext;
import org.sirius.frontend.parser.SParserBaseVisitor;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class FunctionDeclarationParser {
	private Reporter reporter;
	private Parsers parsers;
	private CommonTokenStream tokens;
	
	public FunctionDeclarationParser(Reporter reporter, CommonTokenStream tokens) {
		super();
		this.reporter = reporter;
		this.parsers = new Parsers(reporter, tokens);
		this.tokens = tokens;
	}

	public static class FunctionParameterVisitor extends SParserBaseVisitor<AstFunctionParameter> {
		private Reporter reporter;
		
		public FunctionParameterVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstFunctionParameter visitFunctionDefinitionParameter(FunctionDefinitionParameterContext ctx) {
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			AstType type = typeVisitor.visit(ctx.type());
			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());

			return new AstFunctionParameter(type, name);
		}
	}

	public class FunctionBodyVisitor extends SParserBaseVisitor<List<AstStatement> > {
		private Reporter reporter;
		
		public FunctionBodyVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public List<AstStatement> visitFunctionBody(FunctionBodyContext ctx) {
			StatementParser.StatementVisitor statementVisitor = new StatementParser(reporter, tokens).new StatementVisitor();
			
			List<AstStatement> statements =  ctx.statement().stream()
				.map(stmtCtxt -> stmtCtxt.accept(statementVisitor))
				.collect(Collectors.toList());
			
			return statements;
		}
	}

	public static class TypeParameterListVisitor extends SParserBaseVisitor< List<TypeParameter> > {
		private Reporter reporter;
		
		public TypeParameterListVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		
		@Override
		public List<TypeParameter> visitTypeParameterDeclarationList(TypeParameterDeclarationListContext ctx) {
			TypeParameterParser.TypeParameterVisitor typeParameterVisitor = new TypeParameterParser(reporter).new TypeParameterVisitor();
			
			List<TypeParameter> typeParameters = ctx.typeParameterDeclaration().stream()
					.map(typeParameterVisitor::visit)
					.collect(Collectors.toUnmodifiableList());
			
			return typeParameters;
		}
	}
	
//	public class FunctionDeclarationVisitor extends SParserBaseVisitor<FunctionDeclaration> {
//		private Reporter reporter;
//
//		public FunctionDeclarationVisitor(Reporter reporter) {
//			super();
//			this.reporter = reporter;
//		}
//
//		
//		@Override
//		public FunctionDeclaration visitFunctionDeclaration(FunctionDeclarationContext ctx) {
//			
//			// -- Annotation List
//			AnnotationList annoList = new Parsers.AnnotationListVisitor().visit(ctx.annotationList());
//			
//			AstToken name = new AstToken(ctx.name);
//			
//			// -- Function parameters
//			Parsers.FunctionParameterListVisitor argListVisitor = parsers.new FunctionParameterListVisitor(reporter);
//			
//			List<AstFunctionParameter> functionParams = argListVisitor.visit(ctx.functionDefinitionParameterList());
//			
//			// -- Return type
//			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
//			
//			TypeContext returnContext = ctx.returnType;
//			AstType returnType =  (returnContext == null) ?
//				AstVoidType.instance :
//				typeVisitor.visit(returnContext);
//			
//			boolean member = false; 
//			
//			return new FunctionDeclaration(annoList, functionParams, returnType, member, name) ;
//		}
//	}
	public class FunctionDefinitionVisitor extends SParserBaseVisitor<FunctionDefinition> {
//		private Reporter reporter;

		public FunctionDefinitionVisitor(/*Reporter reporter*/) {
			super();
//			this.reporter = reporter;
		}

		
		@Override
		public FunctionDefinition visitFunctionDefinition(FunctionDefinitionContext ctx) {

			// -- Annotation List
			AnnotationList annoList = new Parsers.AnnotationListVisitor().visit(ctx.annotationList());

			AstToken name = new AstToken(ctx.name);
			
			// -- Function parameters
			Parsers.FunctionParameterListVisitor argListVisitor = (new Parsers(reporter, tokens)).new FunctionParameterListVisitor();
			
			List<AstFunctionParameter> functionParams = argListVisitor.visit(ctx.functionDefinitionParameterList());
			
			// -- Return type
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			TypeContext returnContext = ctx.returnType;
			AstType returnType =  (returnContext == null) ?
				AstVoidType.instance :
				typeVisitor.visit(returnContext);
			
			// -- Body
			assert((ctx.functionBody() != null));
			FunctionBodyVisitor bodyVisitor = new FunctionBodyVisitor(reporter);
			List<AstStatement> bodyStatements = ctx.functionBody().accept(bodyVisitor);
			
			boolean member = false; 
			
			return new FunctionDefinition(annoList, functionParams, returnType, member, name, bodyStatements) ;
		}
	}

}
