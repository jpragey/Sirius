package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.FunctionBodyContext;
import org.sirius.frontend.parser.SiriusParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionParameterContext;
import org.sirius.frontend.parser.SiriusParser.FunctionParameterListContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationListContext;

import com.google.common.collect.ImmutableList;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class FunctionDeclarationParser {
	private Reporter reporter;
	
	public FunctionDeclarationParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class FunctionParameterVisitor extends SiriusBaseVisitor<AstFunctionParameter> {
		private Reporter reporter;
		
		public FunctionParameterVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstFunctionParameter visitFunctionParameter(FunctionParameterContext ctx) {
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			AstType type = typeVisitor.visit(ctx.type());
			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());

			return new AstFunctionParameter(type, name);
		}
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

	public static class FunctionParameterListVisitor extends SiriusBaseVisitor< List<AstFunctionParameter> > {
		private Reporter reporter;
		
		public FunctionParameterListVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		
		@Override
		public List<AstFunctionParameter> visitFunctionParameterList(FunctionParameterListContext ctx) {
			FunctionParameterVisitor v = new FunctionParameterVisitor(reporter);
			
			List<AstFunctionParameter> functionParameters = 
			ctx.functionParameter().stream()
				.map(formalArgCtx -> v.visit(formalArgCtx))
				.collect(Collectors.toList());
			
			int currentArgIndex = 0; // index in argument list
			for(var fp: functionParameters) {
				fp.setIndex(currentArgIndex++);
			}

			return functionParameters;
		}
	}
	
//	public static class TypeParameterVisitor extends SiriusBaseVisitor< TypeParameter> {
//		private Reporter reporter;
//		
//		public TypeParameterVisitor(Reporter reporter) {
//			super();
//			this.reporter = reporter;
//		}
//		@Override
//		public TypeParameter visitTypeParameterDeclaration(TypeParameterDeclarationContext ctx) {
//			ctx.i
//			return super.visitTypeParameterDeclaration(ctx);
//		}
//	}
	public static class TypeParameterListVisitor extends SiriusBaseVisitor< List<TypeParameter> > {
		private Reporter reporter;
		
		public TypeParameterListVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		
		@Override
		public List<TypeParameter> visitTypeParameterDeclarationList(TypeParameterDeclarationListContext ctx) {
			TypeParameterParser.TypeParameterVisitor typeParameterVisitor = new TypeParameterParser.TypeParameterVisitor(reporter);
			
			List<TypeParameter> typeParameters = 
					ctx.typeParameterDeclaration().stream().map(tpdc -> typeParameterVisitor.visit(tpdc))
					.collect(Collectors.toUnmodifiableList());
			
			return typeParameters;
		}
		
//		@Override
//		public List<AstFunctionParameter> visitFunctionParameterList(FunctionParameterListContext ctx) {
//			FunctionParameterVisitor v = new FunctionParameterVisitor(reporter);
//			
//			List<AstFunctionParameter> functionParameters = 
//			ctx.functionParameter().stream()
//				.map(formalArgCtx -> v.visit(formalArgCtx))
//				.collect(Collectors.toList());
//			
//			int currentArgIndex = 0; // index in argument list
//			for(var fp: functionParameters) {
//				fp.setIndex(currentArgIndex++);
//			}
//
//			return functionParameters;
//		}
	}
	public static class FunctionDeclarationVisitor extends SiriusBaseVisitor<PartialList> {
		private Reporter reporter;

		public FunctionDeclarationVisitor(Reporter reporter/*, QName containerQName*/) {
			super();
			this.reporter = reporter;
		}

		
		@Override
		public PartialList visitFunctionDeclaration(FunctionDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.name);
			
			// -- Function parameters
			FunctionParameterListVisitor argListVisitor = new FunctionParameterListVisitor(reporter);
			
			List<AstFunctionParameter> functionParams = argListVisitor.visit(ctx.functionParameterList());
			
			// -- Return type
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			TypeContext returnContext = ctx.returnType;
			AstType returnType =  (returnContext == null) ?
				AstVoidType.instance :
				typeVisitor.visit(returnContext);
			
			// -- Body
			
			Optional<List<AstStatement>> body = Optional.empty();
			if(ctx.functionBody() != null) {
				FunctionBodyVisitor bodyVisitor = new FunctionBodyVisitor(reporter);
				List<AstStatement> statements = ctx.functionBody().accept(bodyVisitor);
				body = Optional.of(statements);
			}
			
			boolean member = false; 
			
			return new PartialList(functionParams, returnType, member, /* qName,*/ /*concrete, */name, body) ;
		}
	}

}
