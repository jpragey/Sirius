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
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionFormalArgumentContext;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.StatementContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationContext;

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

	//AstFunctionParameter
	public static class FunctionParameterVisitor extends SiriusBaseVisitor<AstFunctionParameter> {
		private Reporter reporter;
		
		public FunctionParameterVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstFunctionParameter visitFunctionFormalArgument(FunctionFormalArgumentContext ctx) {
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			AstType type = typeVisitor.visit(ctx.type());
			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());

			return new AstFunctionParameter(type, name);
		}
		
	}
	
	public static class FunctionDeclarationVisitor extends SiriusBaseVisitor<PartialList> {
		private Reporter reporter;
//		private QName containerQName;

		public FunctionDeclarationVisitor(Reporter reporter/*, QName containerQName*/) {
			super();
			this.reporter = reporter;
//			this.containerQName = containerQName;
		}

		
		@Override
		public PartialList visitFunctionDeclaration(FunctionDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.name);
//			QName qName = containerQName.child(name.getText());
			
			// -- Function parameters
			FunctionParameterVisitor paramVisitor = new FunctionParameterVisitor(reporter);
			List<AstFunctionParameter> functionParams = ctx.functionFormalArgument().stream()
				.map(funcParam -> paramVisitor.visitFunctionFormalArgument(funcParam))
				.collect(Collectors.toList());
			
			// -- Return type
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			TypeContext returnContext = ctx.returnType;
			AstType returnType =  (returnContext == null) ?
				AstVoidType.instance :
				typeVisitor.visit(returnContext);
			
			// -- Body
			StatementParser.StatementVisitor statementVisitor = new StatementParser.StatementVisitor(reporter);
			
			// TODO
			Optional<List<AstStatement>> body = Optional.empty();
//			List<StatementContext> statementContexts = ctx.statement();
			if(ctx.statement() != null) {
				List<AstStatement> statements = ctx.statement().stream()
						.map(stmtContext -> stmtContext.accept(statementVisitor))
						.collect(Collectors.toList())
						;
				body = Optional.of(statements);
			}
//			boolean concrete = false; 
			boolean member = false; 
			
			
			return new PartialList(functionParams, returnType, member, /* qName,*/ /*concrete, */name, body) ;
		}


//		@Override
//		public AstInterfaceDeclaration visitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
//			
//			AstToken name = new AstToken(ctx.TYPE_ID(0).getSymbol());
//			
////			AstToken intfName = new AstToken(ctx.TYPE_ID(1).getSymbol());
//			List<AstClassOrInterface.AncestorInfo> intfList = ctx.TYPE_ID().stream()
//				.skip(1)
//				.map(terminalNode -> new AstClassOrInterface.AncestorInfo(new AstToken(terminalNode.getSymbol())))
//				.collect(Collectors.toList());
//			
//			
//			Optional<QName> packageQName = Optional.empty();
//			
//			
//			// -- type parameters
//			TypeParameterParser.TypeParameterVisitor typeParameterVisitor = new TypeParameterParser.TypeParameterVisitor(reporter);
//			List<TypeParameter> typeParameters = ctx.typeParameterDeclaration().stream()
//				.map(typeParamDeclCtxt -> typeParamDeclCtxt.accept(typeParameterVisitor))
//				.collect(Collectors.toUnmodifiableList());
//
//			
////			ImmutableList<TypeParameter> typeParameters = ImmutableList.of();
//			
//			ImmutableList<PartialList> functionDeclarations = ImmutableList.of();
//			
//			AstInterfaceDeclaration interfaceDeclaration = new AstInterfaceDeclaration(reporter, name, packageQName, 
//					functionDeclarations, 
//					ImmutableList.copyOf(typeParameters),
//					ImmutableList.copyOf(intfList));
//			return interfaceDeclaration;
//		}
	}

}
